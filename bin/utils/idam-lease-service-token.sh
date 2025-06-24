#!/bin/bash
## Usage: ./idam-lease-service-token.sh [microservice_name]
##
## Options:
##    - microservice_name: Name of the microservice. Default to `iac`.
##
## Returns a valid IDAM service token for the given microservice.

microservice=${1:-iac}
export SERVICE_AUTH_PROVIDER_API_BASE_URL=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal
curl --silent --show-error -X POST \
  -H "Content-Type: application/json" \
  -d '{"microservice":"'${microservice}'"}' \
  ${SERVICE_AUTH_PROVIDER_API_BASE_URL}/testing-support/lease
