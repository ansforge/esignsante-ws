project = "${workspace.name}"

labels = { "domaine" = "esignsante" }

runner {
  enabled = true
  profile = "common-odr"
  data_source "git" {
    url = "https://github.com/ansforge/esignsante-ws.git"
    ref = "gitref"
    ignore_changes_outside_path = true
  }
  poll {
    enabled = false
  }
} 

# An application to deploy.
app "cybersante-esignsante" {
  # Build specifies how an application should be deployed.
  build {
    use "docker" {
      dockerfile = "${path.app}/${var.dockerfile_path}"
    }

    registry {
      use "docker" {
        image = "${var.registry_path}/esignsante"
        tag   = gitrefpretty()
        username = var.username
        password = var.password
	    }
    }
  }

  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/esignsante.nomad.tpl", {
        datacenter = var.datacenter
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
        min_count = var.min_count
        max_count = var.max_count
        cooldown = var.cooldown
        seuil_scale_in = var.seuil_scale_in
        seuil_scale_out = var.seuil_scale_out
        nomad_namespace = "${workspace.name}"
        nomad_namejob = var.nomad_namejob
      })
    }
  }
}

####
variable "nomad_namejob" {
  type = string
  default = "esignsante"
}
variable datacenter {
  type = string
  default = ""
  env     = ["NOMAD_DC"]
}
variable "username" {
  type      = string
  default   = ""
  env       = ["REGISTRY_USER"]
  sensitive = true
}
variable "password" {
  type      = string
  default   = ""
  env       = ["REGISTRY_PASS"]
  sensitive = true
}
####
variable dockerfile_path {
    type = string
    default = "Dockerfile"
}
variable "registry_path" {
    type = string
    default = "registry.repo.proxy.prod.forge.esante.gouv.fr/esignsante"
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
  default = "true"
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
#
variable "config_crl_scheduling" {
  type = string
  default = ""
}
variable "ignore_line_breaks" {
  type = bool
  default = false
}
variable "min_count" {
  type = number
  # default = 2
  default = 1
}
variable "max_count" {
  type = number
  default = 5
}
variable "cooldown" {
  type = string
  default = "180s"
}
variable "seuil_scale_in" {
  type = number
  default = 1
}
variable "seuil_scale_out" {
  type = number
  default = 5
}