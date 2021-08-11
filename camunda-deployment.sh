#!/bin/bash
## Usage: ./camunda-deployment [SERVICE_TOKEN]
##
## Options:
##    - SERVICE_TOKEN: a service token for a whitelisted service in camunda which is generated with the idam-service-token.
##
## deploys bpmn/dmn to camunda.

BASEDIR=$(dirname "$0")
SERVICE_TOKEN=$1
JURISDICTION="ia"

for file in $BASEDIR/src/main/resources/*.dmn; do
  if [[ $file == *"wacasetype"* ]]; then
    JURISDICTION="wa"
  else JURISDICTION="ia"
  fi
  curl --silent --show-error ${CAMUNDA_URL}/deployment/create \
    -H 'Content-Type: multipart/form-data' \
    -H "ServiceAuthorization: ${SERVICE_TOKEN}" \
    -F "deployment-source=${JURISDICTION}" \
    -F "tenant-id=${JURISDICTION}" \
    -F data=@$file
done
