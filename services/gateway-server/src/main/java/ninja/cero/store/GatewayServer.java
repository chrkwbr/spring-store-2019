package ninja.cero.store;

import ninja.cero.store.accesslog.AccessLoggingGatewayFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@SpringBootApplication
public class GatewayServer {

    private final Logger log = LoggerFactory.getLogger(GatewayServer.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayServer.class, args);
    }

    @Value("${ui.url:${vcap.services.store-ui.credentials.url:http://localhost:8000}}")
    private String uiUrl;

    @Value("${web.url:${vcap.services.store-web.credentials.url:http://localhost:9000}}")
    private String webUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AccessLoggingGatewayFilterFactory accessLoggingFilterFactory, Environment env) {
        log.info("UI: {}, Web: {}", this.uiUrl, this.webUrl);
        return builder.routes()
            .route(r -> r.path("/api/**")
                .filters(f -> (env.acceptsProfiles(Profiles.of("cloud")) ? f : f
                    .filter(accessLoggingFilterFactory.apply(c -> {
                    })))
                    .rewritePath("/api/(?<path>.*)", "/${path}"))
                .uri(this.webUrl)
            )
            .route(r -> (r.path("/**"))
                .filters(f -> (env.acceptsProfiles(Profiles.of("cloud")) ? f : f
                    .filter(accessLoggingFilterFactory.apply(c -> {
                    })))
                    .rewritePath("/(?<path>.*)", "/${path}"))
                .uri(this.uiUrl)
            )
            .build();
    }
}
