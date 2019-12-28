#!/bin/bash
set -ex

KIBANA_HOST=${KIBANA_HOST:-localhost}
KIBANA_PORT=${KIBANA_PORT:-5601}
ELASTIC_USER=${ELASTIC_USER}
ELASTIC_PASSWORD=${ELASTIC_PASSWORD}

if [ -n "${ELASTIC_USER}" ]; then
  CURL_UESR_OPT="-u ${ELASTIC_USER}:${ELASTIC_PASSWORD}"
fi

curl ${CURL_UESR_OPT} -w '\n' -X POST "http://${KIBANA_HOST}:${KIBANA_PORT}/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@`dirname $0`/saved_objects/logstash-index.ndjson
curl ${CURL_UESR_OPT} -w '\n' -X POST "http://${KIBANA_HOST}:${KIBANA_PORT}/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@`dirname $0`/saved_objects/spring-boot-search.ndjson