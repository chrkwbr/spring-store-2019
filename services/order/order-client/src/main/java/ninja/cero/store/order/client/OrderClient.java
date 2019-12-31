package ninja.cero.store.order.client;

import ninja.cero.store.order.domain.OrderEvent;
import ninja.cero.store.order.domain.OrderInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OrderClient {

    private final RestTemplate restTemplate;

    private final String orderUrl;

    ParameterizedTypeReference<List<OrderEvent>> type = new ParameterizedTypeReference<>() {

    };

    public OrderClient(RestTemplate restTemplate, String orderUrl) {
        this.restTemplate = restTemplate;
        this.orderUrl = orderUrl;
    }

    public void createOrder(OrderInfo order) {
        restTemplate.postForObject(this.orderUrl, order, Void.class);
    }

    public void createEvent(OrderEvent orderEvent) {
        restTemplate.postForObject(this.orderUrl + "/{orderId}/event", orderEvent, Void.class, orderEvent.orderId);
    }

    public List<OrderEvent> findAllEvents() {
        return restTemplate.exchange(this.orderUrl + "/events", HttpMethod.GET, null, type).getBody();
    }
}
