package ninja.cero.store.item.app;

import ninja.cero.store.item.domain.Item;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends CrudRepository<Item, Long> {

    @Override
    @Query("SELECT item.id AS id, item.name AS name, item.image AS image, item.media AS media, item.author AS author, item.`release` AS `release`, item.unit_price AS unit_price FROM item")
    Iterable<Item> findAll();

    @Override
    @Query("SELECT item.id AS id, item.name AS name, item.image AS image, item.media AS media, item.author AS author, item.`release` AS `release`, item.unit_price AS unit_price FROM item WHERE item.id IN (:ids)")
    Iterable<Item> findAllById(@Param("ids") Iterable<Long> ids);
}
