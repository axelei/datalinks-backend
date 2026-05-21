package net.krusher.datalinks.application.handler.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlugifyProviderTest {

    @Test
    void slugifyTransliteratesAndSlugifies() {
        String s = SlugifyProvider.SLUGIFY.slugify("Héllo Wörld!");
        assertNotNull(s);
        assertTrue(s.contains("hello"));
        assertTrue(s.contains("world"));
    }
}
