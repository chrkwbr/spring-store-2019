package ninja.cero.store.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class ItemClientAutoConfiguration {

    @ConditionalOnMissingBean
    @ConditionalOnBean(RestTemplate.class)
    @Bean
    public ItemClient itemClient(RestTemplate restTemplate, @Value("${item.url:${vcap.services.item.credentials.url:http://localhost:9001}}") String itemUrl) {
        return new ItemClient(restTemplate, itemUrl);
    }
}
