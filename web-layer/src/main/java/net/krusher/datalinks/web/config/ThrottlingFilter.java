package net.krusher.datalinks.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Provider
public class ThrottlingFilter implements ContainerRequestFilter {

    private final Cache<String, Bucket> buckets;

    @ConfigProperty(name = "app.throttling.capacity", defaultValue = "120")
    int capacity;

    @ConfigProperty(name = "app.throttling.refillTokens", defaultValue = "80")
    int refillTokens;

    @ConfigProperty(name = "app.throttling.refillMinutes", defaultValue = "1")
    int refillMinutes;

    @ConfigProperty(name = "app.throttling.maxSize", defaultValue = "10000")
    int maxSize;

    @ConfigProperty(name = "app.throttling.expireMinutes", defaultValue = "60")
    int expireMinutes;

    @Inject
    RoutingContext routingContext;

    public ThrottlingFilter() {
        this.buckets = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(60))
                .maximumSize(10000)
                .build();
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillTokens, Duration.ofMinutes(refillMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIp = Optional.ofNullable(routingContext)
                .map(rc -> rc.request().remoteAddress())
                .map(SocketAddress::host)
                .orElse("unknown");
        Bucket bucket = buckets.get(clientIp, k -> createNewBucket());
        BlockingBucket blockingBucket = bucket.asBlocking();
        try {
            blockingBucket.consume(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            requestContext.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Too many requests")
                    .build());
        }
    }
}
