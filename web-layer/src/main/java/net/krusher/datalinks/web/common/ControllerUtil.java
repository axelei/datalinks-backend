package net.krusher.datalinks.web.common;

import io.vertx.ext.web.RoutingContext;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.util.UUID;

public class ControllerUtil {
    private static final Logger LOG = Logger.getLogger(ControllerUtil.class);
    public static final String AUTH_HEADER = "Authorization";

    public static UUID toLoginToken(String bearerToken) {
        if (bearerToken == null) {
            return null;
        }
        String token = bearerToken.replaceFirst("^Bearer ", StringUtils.EMPTY);
        return Try.of(() -> UUID.fromString(token))
                .onFailure(e -> LOG.warnf("Invalid auth token format: %s", e.getMessage()))
                .getOrElse(() -> null);
    }

    public static String remoteAddr(RoutingContext rc) {
        if (rc == null || rc.request().remoteAddress() == null) {
            return null;
        }
        return rc.request().remoteAddress().host();
    }
}
