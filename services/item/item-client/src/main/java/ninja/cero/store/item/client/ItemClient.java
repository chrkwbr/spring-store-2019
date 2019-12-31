package ninja.cero.store.item.client;

import ninja.cero.store.item.domain.Item;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemClient {

    private final RestTemplate restTemplate;

    private final String itemUrl;

    ParameterizedTypeReference<List<Item>> type = new ParameterizedTypeReference<>() {

    };

    public ItemClient(RestTemplate restTemplate, String itemUrl) {
        this.restTemplate = restTemplate;
        this.itemUrl = itemUrl;
    }

    public List<Item> findAll() {
        return restTemplate.exchange(this.itemUrl, HttpMethod.GET, null, type).getBody();
    }

    public List<Item> findByIds(Collection<Long> ids) {
        String idString = ids.stream().map(Object::toString)
            .collect(Collectors.joining(","));
        return restTemplate.exchange(this.itemUrl + "/{ids}", HttpMethod.GET, null, type, idString).getBody();
    }
}
