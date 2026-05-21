package net.krusher.datalinks.web.common;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ControllerUtilTest {

    @Test
    void toLoginTokenParsesBearer() {
        UUID id = UUID.randomUUID();
        String bearer = "Bearer " + id.toString();
        UUID res = ControllerUtil.toLoginToken(bearer);
        assertEquals(id, res);
    }

    @Test
    void toLoginTokenReturnsNullForInvalid() {
        UUID res = ControllerUtil.toLoginToken("not-a-uuid");
        assertNull(res);
    }

    @Test
    void toLoginTokenReturnsNullForNullInput() {
        UUID res = ControllerUtil.toLoginToken(null);
        assertNull(res);
    }
}
