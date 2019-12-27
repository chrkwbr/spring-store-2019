package ninja.cero.store.payment.app;

import ninja.cero.store.payment.domain.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, String> {

}
