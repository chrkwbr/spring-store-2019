package ninja.cero.store.accesslog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.REFERER;

@Component
public class AccessLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final Logger log = LoggerFactory.getLogger("RTR");

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            long begin = System.nanoTime();
            return chain.filter(exchange) //
                .doFinally(__ -> {
                    long elapsed = (System.nanoTime() - begin) / 1_000_000;
                    ServerHttpRequest request = exchange.getRequest();
                    ServerHttpResponse response = exchange.getResponse();
                    OffsetDateTime now = OffsetDateTime.now();
                    HttpMethod method = request.getMethod();
                    RequestPath path = request.getPath();
                    HttpStatus code = response.getStatusCode();
                    int statusCode = code == null ? 0 : code.value();
                    HttpHeaders headers = request.getHeaders();
                    String host = headers.getHost().getHostString();
                    String address = request.getRemoteAddress().getHostString();
                    String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
                    String referer = headers.getFirst(REFERER);
                    AccessLog accessLog = new AccessLogBuilder()
                        .setDate(now.toString())
                        .setMethod(Objects.toString(method, ""))
                        .setPath(path.value()).setStatus(statusCode)
                        .setHost(host).setAddress(address).setElapsed(elapsed)
                        .setUserAgent(userAgent).setReferer(referer)
                        .build();
                    log.info("{}", accessLog);
                });
        };
    }
}
