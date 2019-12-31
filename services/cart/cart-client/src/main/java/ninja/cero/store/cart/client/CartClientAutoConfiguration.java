package ninja.cero.store.cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class CartClientAutoConfiguration {

    @ConditionalOnMissingBean
    @ConditionalOnBean(RestTemplate.class)
    @Bean
    public CartClient cartClient(RestTemplate restTemplate, @Value("${cart.url:${vcap.services.cart.credentials.url:http://localhost:9003}}") String cartUrl) {
        return new CartClient(restTemplate, cartUrl);
    }
}
