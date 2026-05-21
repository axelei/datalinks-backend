package net.krusher.datalinks.domain.model.page;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageTest {

    @Test
    void builderAndGettersWork() {
        Page p = Page.builder()
                .slug("s")
                .title("t")
                .summary("sum")
                .categories(Set.of())
                .build();

        assertEquals("s", p.getSlug());
        assertEquals("t", p.getTitle());
        assertEquals("sum", p.getSummary());
    }
}
