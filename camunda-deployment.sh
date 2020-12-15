#!/bin/bash
## Usage: ./camunda-deployment
##
## Options:
##    - AUTHORIZATION: which is generated with the idam-service-token.
##
## deploys bpmn/dmn to camunda.

AUTHORIZATION= sh ${WA_KUBE_ENV_PATH}/scripts/actions/idam-service-token.sh

BASEDIR=$(dirname "$0")

for file in $BASEDIR/src/main/resources/*.bpmn $BASEDIR/src/main/resources/*.dmn
do
	if [ -f "$file" ]
	then
curl --header "Content-Type: multipart/form-data" "ServiceAuthorization: ${AUTHORIZATION}"\
  --request POST \
  --form data=@$file \
  "http://camunda-bpm/engine-rest/deployment/create"
  fi
done
