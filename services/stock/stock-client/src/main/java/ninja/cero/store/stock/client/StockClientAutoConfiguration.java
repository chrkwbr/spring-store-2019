package ninja.cero.store.stock.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class StockClientAutoConfiguration {

    @ConditionalOnMissingBean
    @ConditionalOnBean(RestTemplate.class)
    @Bean
    public StockClient stockClient(RestTemplate restTemplate, @Value("${stock.url:${vcap.services.stock.credentials.url:http://localhost:9002}}") String stockUrl) {
        return new StockClient(restTemplate, stockUrl);
    }
}
