package ninja.cero.store.order;

import ninja.cero.store.order.client.OrderClient;
import ninja.cero.store.order.domain.OrderInfo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    private final OrderClient orderClient;

    public OrderController(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @PostMapping
    public void checkout(@RequestBody OrderInfo order) {
        orderClient.createOrder(order);
    }
}
