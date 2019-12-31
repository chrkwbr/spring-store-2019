package ninja.cero.store.cart.client;

import ninja.cero.store.cart.domain.Cart;
import ninja.cero.store.cart.domain.CartDetail;
import ninja.cero.store.cart.domain.CartEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class CartClient {

    private final RestTemplate restTemplate;

    private final String cartUrl;

    ParameterizedTypeReference<List<Cart>> type = new ParameterizedTypeReference<>() {

    };

    public CartClient(RestTemplate restTemplate, String cartUrl) {
        this.restTemplate = restTemplate;
        this.cartUrl = cartUrl;
    }

    public List<Cart> findAll() {
        return restTemplate.exchange(this.cartUrl, HttpMethod.GET, null, type).getBody();
    }

    public Cart findCartById(String cartId) {
        return restTemplate.getForObject(this.cartUrl + "/{cartId}", Cart.class, cartId);
    }

    public CartDetail findCartDetailById(String cartId) {
        return restTemplate.getForObject(this.cartUrl + "/{cartId}/detail", CartDetail.class, cartId);
    }

    public Cart createCart() {
        return restTemplate.postForObject(this.cartUrl, null, Cart.class);
    }

    public Cart addItem(String cartId, CartEvent cartEvent) {
        return restTemplate.postForObject(this.cartUrl + "/{cartId}", cartEvent, Cart.class, cartId);
    }

    public void removeItem(String cartId, Long itemId) {
        restTemplate.delete(this.cartUrl + "/{cartId}/items/{itemId}", cartId, itemId);
    }
}
