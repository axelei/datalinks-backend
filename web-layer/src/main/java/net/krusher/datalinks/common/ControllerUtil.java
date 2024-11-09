package net.krusher.datalinks.common;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ControllerUtil {
    public static final String AUTH_HEADER = "Authorization";

    public static UUID toLoginToken(String bearerToken) {
        if (bearerToken == null) {
            return null;
        }
        String token = bearerToken.replaceFirst("^Bearer ", StringUtils.EMPTY);
        return UUID.fromString(token);
    }
}