package ninja.cero.store.payment.client;

import ninja.cero.store.payment.domain.Payment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PaymentClient {

    private final RestTemplate restTemplate;

    private final String paymentUrl;

    ParameterizedTypeReference<List<Payment>> type = new ParameterizedTypeReference<>() {

    };

    public PaymentClient(RestTemplate restTemplate, String paymentUrl) {
        this.restTemplate = restTemplate;
        this.paymentUrl = paymentUrl;
    }

    public void check(Payment payment) {
        restTemplate.postForObject(this.paymentUrl + "/check", payment, Void.class);
    }

    public void processPayment(Payment payment) {
        restTemplate.postForObject(this.paymentUrl, payment, Void.class);
    }

    public List<Payment> findAll() {
        return restTemplate.exchange(this.paymentUrl, HttpMethod.GET, null, type).getBody();
    }
}
