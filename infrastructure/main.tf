provider "azurerm" {
  features {}
}

data "azurerm_key_vault" "ia_key_vault" {
  name                = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
}
