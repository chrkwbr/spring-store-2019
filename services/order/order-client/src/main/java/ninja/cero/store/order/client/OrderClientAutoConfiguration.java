package ninja.cero.store.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class OrderClientAutoConfiguration {

    @ConditionalOnMissingBean
    @ConditionalOnBean(RestTemplate.class)
    @Bean
    public OrderClient orderClient(RestTemplate restTemplate, @Value("${order.url:${vcap.services.order.credentials.url:http://localhost:9004}}") String orderUrl) {
        return new OrderClient(restTemplate, orderUrl);
    }
}
