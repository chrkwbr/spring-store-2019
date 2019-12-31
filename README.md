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
cf create-service p.rabbitmq single-node-3.7 payment-mq
```

or for Pivotal Web Services

```
cf create-service cleardb spark cart-db
cf create-service cleardb spark item-db
cf create-service cleardb spark order-db
cf create-service cleardb spark payment-db
cf create-service cleardb spark stock-db
cf create-service cloudamqp lemur payment-mq
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