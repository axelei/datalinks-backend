package net.krusher.datalinks.domain.model.user;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserLevelTest {

    @Test
    void valueOfReturnsOptionalForKnownLevel() {
        Optional<UserLevel> opt = UserLevel.valueOf(10);
        assertTrue(opt.isPresent());
        assertEquals(UserLevel.ADMIN, opt.get());
    }

    @Test
    void valueOfReturnsEmptyForUnknown() {
        Optional<UserLevel> opt = UserLevel.valueOf(999);
        assertFalse(opt.isPresent());
    }
}
