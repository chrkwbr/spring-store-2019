package ninja.cero.store.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class PaymentClientAutoConfiguration {

    @ConditionalOnMissingBean
    @ConditionalOnBean(RestTemplate.class)
    @Bean
    public PaymentClient paymentClient(RestTemplate restTemplate, @Value("${payment.url:${vcap.services.payment.credentials.url:http://localhost:9005}}") String paymentUrl) {
        return new PaymentClient(restTemplate, paymentUrl);
    }
}
