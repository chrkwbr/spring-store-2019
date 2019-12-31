package ninja.cero.store.stock.client;

import ninja.cero.store.stock.domain.Stock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StockClient {

    private final RestTemplate restTemplate;

    private final String stockUrl;

    ParameterizedTypeReference<List<Stock>> type = new ParameterizedTypeReference<>() {

    };

    public StockClient(RestTemplate restTemplate, String stockUrl) {
        this.restTemplate = restTemplate;
        this.stockUrl = stockUrl;
    }

    public List<Stock> findAll() {
        return restTemplate.exchange(this.stockUrl, HttpMethod.GET, null, type).getBody();
    }

    public List<Stock> findByIds(Collection<Long> ids) {
        String idString = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return restTemplate.exchange(this.stockUrl + "/{ids}", HttpMethod.GET, null, type, idString).getBody();
    }

    public void keepStock(List<Stock> keeps) {
        restTemplate.postForObject(this.stockUrl, keeps, Void.class);
    }
}
