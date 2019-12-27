package ninja.cero.store.order.app;

import ninja.cero.store.cart.client.CartClient;
import ninja.cero.store.cart.domain.CartDetail;
import ninja.cero.store.order.domain.EventType;
import ninja.cero.store.order.domain.OrderEvent;
import ninja.cero.store.order.domain.OrderInfo;
import ninja.cero.store.payment.client.PaymentClient;
import ninja.cero.store.payment.domain.Payment;
import ninja.cero.store.stock.client.StockClient;
import ninja.cero.store.stock.domain.Stock;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@EnableBinding(OrderSource.class)
public class OrderController {

    private final OrderRepository orderRepository;

    private final OrderEventRepository orderEventRepository;

    private final OrderSource orderSource;

    private final CartClient cartClient;

    private final StockClient stockClient;

    private final PaymentClient paymentClient;

    public OrderController(OrderRepository orderRepository, OrderEventRepository orderEventRepository, OrderSource orderSource, CartClient cartClient, StockClient stockClient,
                           PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
        this.orderSource = orderSource;
        this.cartClient = cartClient;
        this.stockClient = stockClient;
        this.paymentClient = paymentClient;
    }

    @PostMapping
    public void createOrder(@RequestBody OrderInfo order) {
        orderRepository.save(order);

        CartDetail cart = cartClient.findCartDetailById(order.cartId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        // Keep stock
        List<Stock> keepRequests = cart.items.stream().map(i -> {
            Stock stock = new Stock();
            stock.itemId = i.itemId;
            stock.quantity = i.quantity;
            return stock;
        }).collect(Collectors.toList());
        stockClient.keepStock(keepRequests);

        // Check card
        Payment payment = new Payment();
        payment.name = order.cardName;
        payment.expire = order.cardExpire;
        payment.cardNumber = order.cardNumber;
        payment.amount = cart.total;
        paymentClient.check(payment);

        // Start orderEvent
        OrderEvent event = new OrderEvent();
        event.orderId = order.id;
        event.eventType = EventType.START;
        event.eventTime = new Timestamp(System.currentTimeMillis());
        orderEventRepository.save(event);

        // Order
        orderSource.order().send(MessageBuilder.withPayload(order).build());

        // Payment

        // SendMail

    }

    @PostMapping("/{orderId}/event")
    public void createEvent(@RequestBody OrderEvent orderEvent) {
        orderEventRepository.save(orderEvent);
    }

    @GetMapping("/")
    public Iterable<OrderInfo> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/events")
    public Iterable<OrderEvent> getEvents() {
        return orderEventRepository.findAll();
    }
}
