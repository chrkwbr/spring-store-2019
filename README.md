# spring-store-2019

## How to Run

1. Install `java11` and `docker`, `docker-compose`

2. create docker network
```
docker network create demo
```

3. Run infrastructures
```
cd infra
docker-compose up
```

4. Confirm infrastructures are up
```
curl localhost:9200
curl localhost:5601 -i
```

5. Configure Kibana (only for initialization)
```
bash ./infra/kibana/setup.sh
```

6. Build and run services
```
cd services
./build.sh
docker-compose up
```

7. Open [http://localhost:8761](http://localhost:8761) in browser and check all services are available

8. Open [http://localhost:8080](http://localhost:8080) in browser

## Observability

* Kibana ... [http://localhost:5601](http://localhost:5601)
* Zipkin ... [http://localhost:9411](http://localhost:9411)
* Grafana ... [http://localhost:3000](http://localhost:3000) (`admin`:`admin`)
* Prometheus ... [http://localhost:9090](http://localhost:9090)

## Deploy to Cloud Foundry

Create backend services

```
cf create-service p.mysql db-small cart-db
cf create-service p.mysql db-small item-db
cf create-service p.mysql db-small order-db
cf create-service p.mysql db-small payment-db
cf create-service p.mysql db-small stock-db
cf create-service p.rabbitmq single-node-3.7 order-mq
```

or for Pivotal Web Services

```
cf create-service cleardb spark cart-db
cf create-service cleardb spark item-db
cf create-service cleardb spark order-db
cf create-service cleardb spark payment-db
cf create-service cleardb spark stock-db
cf create-service cloudamqp lemur order-mq
```

Deploy Zipkin

```
cf push zipkin --docker-image openzipkin/zipkin-slim -m 512m --random-route --no-start
cf set-env zipkin MEM_MAX_SPANS 100000
cf start zipkin

ZIPKIN_HOST=$(cf curl /v2/apps/$(cf app zipkin --guid)/routes | jq -r '.resources[0].entity.host')
ZIPKIN_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app zipkin --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service zipkin -p "{\"url\":\"https://${ZIPKIN_HOST}.${ZIPKIN_DOMAIN}\"}"
```

Build apps

```
cd services
./build.sh
```

For the first time, deploy services int the following order

```
cf push -f item/item-service/manifest.yml 
ITEM_HOST=$(cf curl /v2/apps/$(cf app item --guid)/routes | jq -r '.resources[0].entity.host')
ITEM_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app item --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service item -p "{\"url\":\"https://${ITEM_HOST}.${ITEM_DOMAIN}\"}"
```

```
cf push -f stock/stock-service/manifest.yml 
STOCK_HOST=$(cf curl /v2/apps/$(cf app stock --guid)/routes | jq -r '.resources[0].entity.host')
STOCK_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app stock --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service stock -p "{\"url\":\"https://${STOCK_HOST}.${STOCK_DOMAIN}\"}"
```

```
cf push -f payment/payment-service/manifest.yml 
PAYMENT_HOST=$(cf curl /v2/apps/$(cf app payment --guid)/routes | jq -r '.resources[0].entity.host')
PAYMENT_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app payment --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service payment -p "{\"url\":\"https://${PAYMENT_HOST}.${PAYMENT_DOMAIN}\"}"
```

```
cf push -f cart/cart-service/manifest.yml 
CART_HOST=$(cf curl /v2/apps/$(cf app cart --guid)/routes | jq -r '.resources[0].entity.host')
CART_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app cart --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service cart -p "{\"url\":\"https://${CART_HOST}.${CART_DOMAIN}\"}"
```

```
cf push -f order/order-service/manifest.yml 
ORDER_HOST=$(cf curl /v2/apps/$(cf app order --guid)/routes | jq -r '.resources[0].entity.host')
ORDER_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app order --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service order -p "{\"url\":\"https://${ORDER_HOST}.${ORDER_DOMAIN}\"}"
```

```
cf push -f store-web/manifest.yml 
STORE_WEB_HOST=$(cf curl /v2/apps/$(cf app store-web --guid)/routes | jq -r '.resources[0].entity.host')
STORE_WEB_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app store-web --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service store-web -p "{\"url\":\"https://${STORE_WEB_HOST}.${STORE_WEB_DOMAIN}\"}"
```

and deploy UI

```
cd ui
npm run build

cf push
STORE_UI_HOST=$(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.host')
STORE_UI_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service store-ui -p "{\"url\":\"https://${STORE_UI_HOST}.${STORE_UI_DOMAIN}\"}"

cd ..
```

Finally deploy the gateway

```
cf push -f gateway-server/manifest.yml 
```

### In case of using C2C networking

> Delete all apps, routes and user-provided-services deployed above except for Zipkin

```
cf push -f item/item-service/manifest.yml -d apps.internal
ITEM_HOST=$(cf curl /v2/apps/$(cf app item --guid)/routes | jq -r '.resources[0].entity.host')
ITEM_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app item --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service item -p "{\"url\":\"http://${ITEM_HOST}.${ITEM_DOMAIN}:8080\"}"
```

```
cf push -f stock/stock-service/manifest.yml -d apps.internal
STOCK_HOST=$(cf curl /v2/apps/$(cf app stock --guid)/routes | jq -r '.resources[0].entity.host')
STOCK_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app stock --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service stock -p "{\"url\":\"http://${STOCK_HOST}.${STOCK_DOMAIN}:8080\"}"
```

```
cf push -f payment/payment-service/manifest.yml -d apps.internal
PAYMENT_HOST=$(cf curl /v2/apps/$(cf app payment --guid)/routes | jq -r '.resources[0].entity.host')
PAYMENT_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app payment --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service payment -p "{\"url\":\"http://${PAYMENT_HOST}.${PAYMENT_DOMAIN}:8080\"}"
```

```
cf push -f cart/cart-service/manifest.yml -d apps.internal
CART_HOST=$(cf curl /v2/apps/$(cf app cart --guid)/routes | jq -r '.resources[0].entity.host')
CART_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app cart --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service cart -p "{\"url\":\"http://${CART_HOST}.${CART_DOMAIN}:8080\"}"
cf add-network-policy cart --destination-app item --protocol tcp --port 8080
```

```
cf push -f order/order-service/manifest.yml -d apps.internal
ORDER_HOST=$(cf curl /v2/apps/$(cf app order --guid)/routes | jq -r '.resources[0].entity.host')
ORDER_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app order --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service order -p "{\"url\":\"http://${ORDER_HOST}.${ORDER_DOMAIN}:8080\"}"
cf add-network-policy order --destination-app stock --protocol tcp --port 8080
cf add-network-policy order --destination-app cart --protocol tcp --port 8080
cf add-network-policy order --destination-app payment --protocol tcp --port 8080
```

```
cf push -f store-web/manifest.yml -d apps.internal
STORE_WEB_HOST=$(cf curl /v2/apps/$(cf app store-web --guid)/routes | jq -r '.resources[0].entity.host')
STORE_WEB_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app store-web --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service store-web -p "{\"url\":\"http://${STORE_WEB_HOST}.${STORE_WEB_DOMAIN}:8080\"}"
cf add-network-policy store-web --destination-app item --protocol tcp --port 8080
cf add-network-policy store-web --destination-app stock --protocol tcp --port 8080
cf add-network-policy store-web --destination-app cart --protocol tcp --port 8080
cf add-network-policy store-web --destination-app order --protocol tcp --port 8080
```

and deploy UI

```
cd ui
npm run build

cf push -d apps.internal
STORE_UI_HOST=$(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.host')
STORE_UI_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service store-ui -p "{\"url\":\"http://${STORE_UI_HOST}.${STORE_UI_DOMAIN}:8080\"}"

cd ..
```

Finally deploy the gateway

```
cf push -f gateway-server/manifest.yml 
cf add-network-policy store --destination-app store-web --protocol tcp --port 8080
cf add-network-policy store --destination-app store-ui --protocol tcp --port 8080
```

### Prometheus RSocket Proxy

If you want to deploy Prometheus RSocket Proxy, it requires TCP Routing

```
SPACE_NAME=$(cat ~/.cf/config.json | jq -r .SpaceFields.Name)
APPS_DOMAIN=$(cf domains | grep apps | head -1 | awk '{print $1}')
TCP_DOMAIN=$(cf domains | grep tcp | head -1 | awk '{print $1}')
PRORXY_HOST=prometheus-proxy # should be unique
TCP_PORT=10014 # should be unique

cf create-route ${SPACE_NAME} ${APPS_DOMAIN} --hostname ${PRORXY_HOST}
cf create-route ${SPACE_NAME} ${TCP_DOMAIN} --port ${TCP_PORT}

cf push prometheus-proxy --docker-image micrometermetrics/prometheus-rsocket-proxy:0.9.0 --no-route

APP_GUID=$(cf app prometheus-proxy --guid)
HTTP_ROUTE_GUID=$(cf curl /v2/routes?q=host:${PRORXY_HOST} | jq -r .resources[0].metadata.guid)
TCP_ROUTE_GUID=$(cf curl /v2/routes?q=port:${TCP_PORT} | jq -r .resources[0].metadata.guid)

cf curl /v2/apps/${APP_GUID} -X PUT -d "{\"ports\": [8080, 7001]}"

cf curl /v2/route_mappings -X POST -d "{\"app_guid\": \"${APP_GUID}\", \"route_guid\": \"${HTTP_ROUTE_GUID}\", \"app_port\": 8080}"
cf curl /v2/route_mappings -X POST -d "{\"app_guid\": \"${APP_GUID}\", \"route_guid\": \"${TCP_ROUTE_GUID}\", \"app_port\": 7001}"
```

Configure apps

```
for APP in cart item order payment stock store store-web;do
  cf set-env ${APP} MANAGEMENT_METRICS_EXPORT_PROMETHEUS_RSOCKET_HOST ${TCP_DOMAIN}
  cf set-env ${APP} MANAGEMENT_METRICS_EXPORT_PROMETHEUS_RSOCKET_PORT ${TCP_PORT}
  cf restart ${APP}
done
```

### Deploy Prometheus

> WARNING: The prometheus instance deployed via this instruction is ephemeral. All data will be lost when restarted.

```
cd infra/prometheus

PROMETHEUS_VERSION=2.15.1
if [ ! -d prometheus-${PROMETHEUS_VERSION}.linux-amd64 ];then
    wget https://github.com/prometheus/prometheus/releases/download/v${PROMETHEUS_VERSION}/prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz
    tar xzf prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz
    rm -f prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz
fi

PROXY_HOST=$(cf curl /v2/apps/$(cf app prometheus-proxy --guid)/routes | jq -r '.resources[0].entity.host')
PROXY_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app prometheus-proxy --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
ZIPKIN_HOST=$(cf curl /v2/apps/$(cf app zipkin --guid)/routes | jq -r '.resources[0].entity.host')
ZIPKIN_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app zipkin --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')

cat prometheus.yml | sed -e "s/prometheus-proxy:8080/${PROXY_HOST}.${PROXY_DOMAIN}:80/g" -e "s/zipkin-server:9411/${ZIPKIN_HOST}.${ZIPKIN_DOMAIN}:80/g" > ./prometheus-${PROMETHEUS_VERSION}.linux-amd64/prometheus.yml

cf push prometheus -k 2G -b binary_buildpack -p ./prometheus-${PROMETHEUS_VERSION}.linux-amd64 --random-route -c "./prometheus --web.listen-address=:8080 --config.file=./prometheus.yml"

cd ../..
```

### Deploy Grafana

> WARNING: The grafana instance deployed via this instruction is ephemeral. All data will be lost when restarted.

```
cd infra/grafana

GRAFANA_VERSION=6.5.2
if [ ! -d grafana-${GRAFANA_VERSION} ];then
    wget https://dl.grafana.com/oss/release/grafana-${GRAFANA_VERSION}.linux-amd64.tar.gz
    tar xzf grafana-${GRAFANA_VERSION}.linux-amd64.tar.gz 
    rm -f grafana-${GRAFANA_VERSION}.linux-amd64.tar.gz 
fi

sed -i '' -e 's|^http_port = 3000$|http_port = 8080|' grafana-${GRAFANA_VERSION}/conf/defaults.ini

PROMETHEUS_HOST=$(cf curl /v2/apps/$(cf app prometheus --guid)/routes | jq -r '.resources[0].entity.host')
PROMETHEUS_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app prometheus --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')

cat provisioning/datasources/datasources.yaml | sed -e "s|http://prometheus:9090|https://${PROMETHEUS_HOST}.${PROMETHEUS_DOMAIN}|g" > grafana-${GRAFANA_VERSION}/conf/provisioning/datasources/datasources.yaml
cp -r provisioning/dashboards/* grafana-${GRAFANA_VERSION}/conf/provisioning/dashboards/
sed -i '' -e 's|/etc/grafana/|/home/vcap/app/conf/|g' grafana-${GRAFANA_VERSION}/conf/provisioning/dashboards/dashboards.yaml

cf push grafana -b binary_buildpack -p ./grafana-${GRAFANA_VERSION} --random-route -c "./bin/grafana-server -config=./conf/defaults.ini"

cd ../..
```