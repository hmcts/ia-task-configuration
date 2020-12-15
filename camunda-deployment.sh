#!/bin/bash
## Usage: ./camunda-deployment [SERVICE_TOKEN]
##
## Options:
##    - SERVICE_TOKEN: a service token for a whitelisted service in camunda which is generated with the idam-service-token.
##
## deploys bpmn/dmn to camunda.


BASEDIR=$(dirname "$0")
SERVICE_TOKEN=$1

for file in $BASEDIR/src/main/resources/*.bpmn $BASEDIR/src/main/resources/*.dmn; do
  if [ -f "$file" ]; then
    curl --header "Content-Type: multipart/form-data" "ServiceAuthorization: ${SERVICE_TOKEN}" \
      --request POST \
      --form data=@$file \
      "${CAMUNDA_URL}/deployment/create"
  fi
done

