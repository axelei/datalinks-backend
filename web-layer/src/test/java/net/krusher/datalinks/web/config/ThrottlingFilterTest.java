package net.krusher.datalinks.web.config;

import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.mockito.ArgumentMatchers;

class ThrottlingFilterTest {

    @Test
    void filterAbortsOnInterruptedException() throws Exception {
        ThrottlingFilter filter = new ThrottlingFilter();

        // mock routing context and request
        RoutingContext rc = Mockito.mock(RoutingContext.class);
        HttpServerRequest req = Mockito.mock(HttpServerRequest.class);
        SocketAddress sa = Mockito.mock(SocketAddress.class);
        Mockito.when(rc.request()).thenReturn(req);
        Mockito.when(req.remoteAddress()).thenReturn(sa);
        Mockito.when(sa.host()).thenReturn("1.2.3.4");

        // inject routingContext
        Field f = ThrottlingFilter.class.getDeclaredField("routingContext");
        f.setAccessible(true);
        f.set(filter, rc);

        // put a Bucket that throws InterruptedException when consume is called
        Bucket bucket = Mockito.mock(Bucket.class);
        BlockingBucket blocking = Mockito.mock(BlockingBucket.class);
        Mockito.doThrow(new InterruptedException()).when(blocking).consume(1);
        Mockito.when(bucket.asBlocking()).thenReturn(blocking);

        Field mapField = ThrottlingFilter.class.getDeclaredField("buckets");
        mapField.setAccessible(true);
        ConcurrentHashMap<String, Bucket> buckets = (ConcurrentHashMap<String, Bucket>) mapField.get(filter);
        buckets.put("1.2.3.4", bucket);

        ContainerRequestContext crc = Mockito.mock(ContainerRequestContext.class);

        filter.filter(crc);

        Mockito.verify(crc).abortWith(ArgumentMatchers.any());
    }
}
