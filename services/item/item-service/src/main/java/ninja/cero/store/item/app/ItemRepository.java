package ninja.cero.store.item.app;

import ninja.cero.store.item.domain.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {

}
