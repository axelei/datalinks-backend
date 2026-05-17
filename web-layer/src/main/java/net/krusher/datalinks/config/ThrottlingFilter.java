package net.krusher.datalinks.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Provider
public class ThrottlingFilter implements ContainerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Inject
    RoutingContext routingContext;

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(120)
                .refillGreedy(80, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIp = Optional.ofNullable(routingContext)
                .map(rc -> rc.request().remoteAddress())
                .map(addr -> addr.host())
                .orElse("unknown");
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());
        BlockingBucket blockingBucket = bucket.asBlocking();
        try {
            blockingBucket.consume(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Too many requests")
                    .build());
        }
    }
}
