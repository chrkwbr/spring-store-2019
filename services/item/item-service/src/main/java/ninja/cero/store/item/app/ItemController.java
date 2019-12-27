package ninja.cero.store.item.app;

import ninja.cero.store.item.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Iterable<Item> findAll() {
        return itemRepository.findAll();
    }

    @GetMapping("/{ids}")
    public Iterable<Item> findByIds(@PathVariable List<Long> ids) {
        return itemRepository.findAllById(ids);
    }
}
