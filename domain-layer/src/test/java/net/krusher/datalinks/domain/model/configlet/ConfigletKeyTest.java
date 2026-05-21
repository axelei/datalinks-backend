package net.krusher.datalinks.domain.model.configlet;

import net.krusher.datalinks.domain.model.user.UserLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigletKeyTest {

    @Test
    void defaultValuesMatchUserLevelNames() {
        assertEquals(UserLevel.USER.name(), ConfigletKey.EDIT_LEVEL.getDefaultValue());
        assertEquals(UserLevel.ADMIN.name(), ConfigletKey.DELETE_LEVEL.getDefaultValue());
    }
}
