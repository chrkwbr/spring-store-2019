package ninja.cero.store.payment.app;

import ninja.cero.store.order.domain.OrderInfo;
import ninja.cero.store.payment.domain.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @StreamListener("order")
    public void hoge(OrderInfo order) throws InterruptedException {
        log.info("Received order: {}", order);
        Thread.sleep(1000L);
    }

    @PostMapping("/check")
    public void check(@RequestBody Payment payment) {
        // Do nothing.
    }

    @PostMapping
    public void processPayment(@RequestBody Payment payment) {
        paymentRepository.save(payment);
    }

    @GetMapping
    public Iterable<Payment> payments() {
        return paymentRepository.findAll();
    }
}
