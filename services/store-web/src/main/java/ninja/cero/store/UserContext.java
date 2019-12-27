package ninja.cero.store;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserContext {

    public String cartId;
}
