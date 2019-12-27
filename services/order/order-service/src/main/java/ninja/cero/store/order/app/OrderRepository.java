package ninja.cero.store.order.app;

import ninja.cero.store.order.domain.OrderInfo;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderInfo, String> {

}
