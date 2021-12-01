project = "cybersante/esignsante"

# Labels can be specified for organizational purposes.
labels = { "domaine" = "esignsante" }

runner {
  enabled = true
  data_source "git" {
    url = "https://github.com/ansforge/esignsante-ws.git"
    ref = var.datacenter
  }
  poll {
    enabled = true
  }
}

# An application to deploy.
app "cybersante/esignsante" {

  # Build specifies how an application should be deployed.
  build {
    use "docker" {
      dockerfile = "${path.app}/${var.dockerfile_path}"
    }

    registry {
      use "docker" {
        image = "${var.registry_path}/esignsante"
        tag   = gitrefpretty()
		encoded_auth = filebase64("/secrets/dockerAuth.json")
	  }
    }
  }

  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/esignsante.nomad.tpl", {
	datacenter = var.datacenter
	proxy_host = var.proxy_host
	proxy_port = var.proxy_port
	user_java_opts = var.user_java_opts
	swagger_ui = var.swagger_ui
	promotion_auto = var.promotion_auto
	spring_http_multipart_max_file_size = var.spring_http_multipart_max_file_size
	spring_http_multipart_max_request_size = var.spring_http_multipart_max_request_size
	hashing_algorithm = var.hashing_algorithm
	appserver_mem_size = var.appserver_mem_size
	config_secret = var.config_secret
	config_crl_scheduling = var.config_crl_scheduling
	ignore_line_breaks = var.ignore_line_breaks
	logstash_host = var.logstash_host
      })
    }
  }
}

variable datacenter {
    type = string
    default = "test"
}

variable dockerfile_path {
    type = string
    default = "Dockerfile"
}

variable "registry_path" {
    type = string
    default = "registry.repo.proxy-dev-forge.asip.hst.fluxus.net/esignsante"
}

variable "proxy_host" {
  type = string
  default = ""
}

variable "proxy_port" {
  type = string
  default = ""
}

variable "user_java_opts" {
  type = string
  default = ""
}

variable "swagger_ui" {
  type = string
  default = ""
}

variable "promotion_auto" {
  type = bool
  default = false
}

variable "spring_http_multipart_max_file_size" {
  type = string
  default = "200MB"
}

variable "spring_http_multipart_max_request_size" {
  type = string
  default = "200MB"
}

variable "hashing_algorithm" {
  type = string
  default = "BCRYPT"
}

variable "appserver_mem_size" {
  type = string
  default = "9216"
}

variable "config_secret" {
  type = string
  default = "enable"
}

variable "config_crl_scheduling" {
  type = string
  default = ""
}

variable "ignore_line_breaks" {
  type = bool
  default = false
}

variable "logstash_host" {
  type = string
  default = ""
}
