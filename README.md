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
cf create-user-provided-service metrics-endpoint-actuator-prometheus -l metrics-endpoint:///actuator/prometheus
```

or for Pivotal Web Services

```
cf create-service cleardb spark cart-db
cf create-service cleardb spark item-db
cf create-service cleardb spark order-db
cf create-service cleardb spark payment-db
cf create-service cleardb spark stock-db
cf create-service cloudamqp lemur payment-mq
cf create-user-provided-service metrics-endpoint-actuator-prometheus -l metrics-endpoint:///actuator/prometheus
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

Deploy Eureka first

```
cf push -f discovery-server/manifest.yml 
EUREKA_HOST=$(cf curl /v2/apps/$(cf app eureka --guid)/routes | jq -r '.resources[0].entity.host')
EUREKA_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app eureka --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service discovery -p "{\"url\":\"https://${EUREKA_HOST}.${EUREKA_DOMAIN}\"}"
```


and deploy UI

```
cd ui
npm run build

cf push
UI_HOST=$(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.host')
UI_DOMAIN=$(cf curl $(cf curl /v2/apps/$(cf app store-ui --guid)/routes | jq -r '.resources[0].entity.domain_url') | jq -r '.entity.name')
cf create-user-provided-service store-ui -p "{\"url\":\"https://${UI_HOST}.${UI_DOMAIN}\"}"

cd ..
```

then all services

```
cf push -f gateway-server/manifest.yml 
cf push -f cart/cart-service/manifest.yml
cf push -f item/item-service/manifest.yml
cf push -f stock/stock-service/manifest.yml
cf push -f order/order-service/manifest.yml
cf push -f payment/payment-service/manifest.yml
cf push -f store-web/manifest.yml 
```
