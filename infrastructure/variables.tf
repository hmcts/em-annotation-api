variable product {
  default = "em"
}

variable shared_product_name {
  default = "rpa"
}

variable component {
  default = "anno"
}

variable location {
  default = "UK South"
}

variable env {
}

variable subscription {
}

variable tenant_id {}

variable jenkins_AAD_objectId {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable common_tags {
  type = map(string)
}
////////////////////////////////////////////////
//Addtional Vars ///////////////////////////////
////////////////////////////////////////////////
variable capacity {
  default = "1"
}

variable java_opts {
  default = ""
}
////////////////////////////////////////////////
// Endpoints
////////////////////////////////////////////////
variable idam_api_base_uri {
  default = "http://betaDevBccidamAppLB.reform.hmcts.net:80"
}

variable open_id_api_base_uri {
  default = "idam-api"
}

variable oidc_issuer_base_uri {
  default = "idam-api"
}

variable s2s_url {
  default = "rpe-service-auth-provider"
}

variable dm_store_app_url {
  default = "dm-store"
}

variable em_anno_app_url {
  default = "em-anno"
}

variable postgresql_user {
  default = "annotation"
}

variable database_name {
  default = "annotation"
}

variable managed_identity_object_id {
  default = ""
}

variable appinsights_location {
  default     = "West Europe"
  description = "Location for Application Insights"
}

variable application_type {
  default     = "web"
  description = "Type of Application Insights (Web/Other)"
}
////////////////////////////////////////////////
// Toggle Features
////////////////////////////////////////////////
variable enable_idam_healthcheck {
    default = "false"
}

variable enable_s2s_healthcheck {
    default = "false"
}

////////////////////////////////////////////////
// db11
////////////////////////////////////////////////
variable postgresql_user_v11 {
  default = "annotation"
}

variable database_name_v11 {
  default = "annotation"
}