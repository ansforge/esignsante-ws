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
      })
    }
  }
}

variable "datacenter" {
  type    = string
  default = "dc1"
}

variable "dockerfile_path" {
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
