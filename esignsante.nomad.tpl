job "esignsante" {
        datacenters = ["${datacenter}"]
        type = "service"
        vault {
                policies = ["esignsante"]
                change_mode = "noop"
        }

        group "esignsante-servers" {
                count = "1"
                restart {
                        attempts = 3
                        delay = "60s"
                        interval = "1h"
                        mode = "fail"
                }

                network {
                        port "http" { to = 8080 }
                }

                update {
                        max_parallel      = 1
                        canary            = 1
                        min_healthy_time  = "30s"
                        progress_deadline = "5m"
                        healthy_deadline  = "2m"
                        auto_revert       = true
                        auto_promote      = ${promotion_auto}
                }

                scaling {
                        enabled = true
                        min     = ${min_count}
                        max     = ${max_count}

			policy {
				# On sélectionne l'instance la moins chargée de toutes les instances en cours,
				# on rajoute une instance (ou on en enlève une) si les seuils spécifiés de requêtes
				# par seconde sont franchis. On pondère le résultat par la consommation de CPU 
				# pour éviter de créer une instance lors du traitement de gros fichiers par esignsante.
                                cooldown = "${cooldown}"
                                check "few_requests" {
                                        source = "prometheus"
                                        query = "min(max(http_server_requests_seconds_max{_app='esignsante'}!= 0)by(instance))*max(process_cpu_usage{_app='esignsante'})"
                                        strategy "threshold" {
                                                upper_bound = ${seuil_scale_in}
                                                delta = -1
                                        }
                                }

                                check "many_requests" {
                                        source = "prometheus"
                                        query = "min(max(http_server_requests_seconds_max{_app='esignsante'}!= 0)by(instance))*max(process_cpu_usage{_app='esignsante'})"
                                        strategy "threshold" {
                                                lower_bound = ${seuil_scale_out}
                                                delta = 1
                                        }
                                }
                        }
                }

                task "run" {
                        env {
                                JAVA_TOOL_OPTIONS="${user_java_opts} -Dspring.config.location=/var/esignsante/application.properties -Dspring.profiles.active=${swagger_ui} -Dhttp.proxyHost=${proxy_host} -Dhttps.proxyHost=${proxy_host} -Dhttp.proxyPort=${proxy_port} -Dhttps.proxyPort=${proxy_port}"
                        }
                        driver = "docker"
                        config {
                                image = "${artifact.image}:${artifact.tag}"
                                volumes = ["secrets:/var/esignsante"]
                                args = [
                                        "--ws.conf=/var/esignsante/config.json",
                                        "--ws.hashAlgo=${hashing_algorithm}",
                                ]
                                ports = ["http"]
                        }
                        template {
data = <<EOH
{
   "signature": [ {{ $length := secrets "esignsante/metadata/signature" | len }}{{ $i := 1 }}{{ range secrets "esignsante/metadata/signature" }}
{{ with secret (printf "esignsante/data/signature/%s" .) }}{{ .Data.data | explodeMap | toJSONPretty | indent 4 }} {{ if lt $i $length }}, {{ end }} {{ end }} {{ $i = add 1 $i }} {{ end }}
  ],
   "proof": [ {{ $length := secrets "esignsante/metadata/proof" | len }}{{ $i := 1 }}{{ range secrets "esignsante/metadata/proof" }}
{{ with secret (printf "esignsante/data/proof/%s" .) }}{{ .Data.data | explodeMap | toJSONPretty | indent 4 }}{{ if lt $i $length }}, {{ end }} {{ end }} {{ $i = add 1 $i }} {{ end }}
  ],
   "signatureVerification": [ {{ $length := secrets "esignsante/metadata/signatureVerification" | len }}{{ $i := 1 }}{{ range secrets "esignsante/metadata/signatureVerification" }}
{{ with secret (printf "esignsante/data/signatureVerification/%s" .) }}{{ .Data.data | explodeMap | toJSONPretty | indent 4 }}{{ if lt $i $length }}, {{ end }} {{ end }} {{ $i = add 1 $i }} {{ end }}
  ],
   "certificateVerification": [ {{ $length := secrets "esignsante/metadata/certificateVerification" | len }}{{ $i := 1 }}{{ range secrets "esignsante/metadata/certificateVerification" }}
{{ with secret (printf "esignsante/data/certificateVerification/%s" .) }}{{ .Data.data | explodeMap | toJSONPretty | indent 4 }}{{ if lt $i $length }}, {{ end }} {{ end }} {{ $i = add 1 $i }} {{ end }}
  ],
   "ca": [ {{ $length := secrets "esignsante/metadata/ca" | len }}{{ $i := 1 }}{{ range secrets "esignsante/metadata/ca" }}
{{ with secret (printf "esignsante/data/ca/%s" .) }}{{ .Data.data | explodeMap | toJSONPretty | indent 4 }}{{ if lt $i $length }}, {{ end }} {{ end }} {{ $i = add 1 $i }} {{ end }}
  ]
}
EOH

                        destination = "secrets/config.json"
                        change_mode = "noop" # noop
                        }
                        template {
data = <<EOF
spring.servlet.multipart.max-file-size=${spring_http_multipart_max_file_size}
spring.servlet.multipart.max-request-size=${spring_http_multipart_max_request_size}
config.secret=${config_secret}
#config.crl.scheduling=${config_crl_scheduling}
server.servlet.context-path=/esignsante/v1
com.sun.org.apache.xml.internal.security.ignoreLineBreaks=${ignore_line_breaks}
management.endpoints.web.exposure.include=prometheus,metrics,health
EOF
                        destination = "secrets/application.properties"
                        }
                        resources {
                                cpu = 1000
                                memory = ${appserver_mem_size}
                        }
                        service {
                                name = "$\u007BNOMAD_JOB_NAME\u007D"
                                tags = ["urlprefix-/esignsante/v1/"]
                                canary_tags = ["canary instance to promote"]
                                port = "http"
                                check {
                                        type = "http"
                                        port = "http"
                                        path = "/esignsante/v1/ca"
					header {
						Accept = ["application/json"]
					}
                                        name = "alive"
                                        interval = "30s"
                                        timeout = "2s"
                                }
                        }
                        service {
                                name = "metrics-exporter"
                                port = "http"
                                tags = ["_endpoint=/esignsante/v1/actuator/prometheus",
                                                                "_app=esignsante",]
                        }
                }
		
# begin log-shipper
# Ce bloc doit être décommenté pour définir le log-shipper.
# Penser à remplir la variable logstash_host.
#        task "log-shipper" {
#			driver = "docker"
#			restart {
#				interval = "30m"
#				attempts = 5
#				delay    = "15s"
#				mode     = "delay"
#			}
#			meta {
#				INSTANCE = "$\u007BNOMAD_ALLOC_NAME\u007D"
#			}
#			template {
#				data = <<EOH
#LOGSTASH_HOST = "${logstash_host}"
#ENVIRONMENT = "${datacenter}"
#EOH
#				destination = "local/file.env"
#				env = true
#			}
#			config {
#				image = "ans/nomad-filebeat:latest"
#			}
#	    }
# end log-shipper
        }
}
