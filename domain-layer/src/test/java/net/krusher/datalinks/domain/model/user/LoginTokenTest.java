package net.krusher.datalinks.domain.model.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginTokenTest {

    @Test
    void builderSetsFields() {
        UUID uid = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        LoginToken r = LoginToken.builder()
                .userId(uid)
                .loginToken(token)
                .creationDate(Instant.now())
                .build();

        assertEquals(uid, r.getUserId());
        assertEquals(token, r.getLoginToken());
        assertNotNull(r.getCreationDate());
    }
}
