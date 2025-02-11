provider "azurerm" {
  features {}
}

data "azurerm_key_vault" "ia_key_vault" {
  name                = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
}

data "azurerm_key_vault_secret" "app_insights_connection_string" {
  name      = "ia-app-insights-connection-string"
  key_vault_id = data.azurerm_key_vault.ia_key_vault.id
}

resource "azurerm_key_vault_secret" "local_app_insights_connection_string" {
  name         = "app-insights-connection-string"
  value        = data.azurerm_key_vault_secret.app_insights_connection_string.value
  key_vault_id = data.azurerm_key_vault.ia_key_vault.id
}
