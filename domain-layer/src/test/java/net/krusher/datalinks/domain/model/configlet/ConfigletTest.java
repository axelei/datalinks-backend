package net.krusher.datalinks.domain.model.configlet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigletTest {

    @Test
    void ofCreatesConfigletWithKeyAndValue() {
        Configlet c = Configlet.of(ConfigletKey.EDIT_LEVEL, "CUSTOM");
        assertNotNull(c);
        assertEquals(ConfigletKey.EDIT_LEVEL, c.getKey());
        assertEquals("CUSTOM", c.getValue());
    }
}
