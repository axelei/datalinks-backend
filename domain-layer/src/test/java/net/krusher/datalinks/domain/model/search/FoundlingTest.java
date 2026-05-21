package net.krusher.datalinks.domain.model.search;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoundlingTest {

    @Test
    void builderSetsFields() {
        Foundling f = Foundling.builder()
                .title("T")
                .content("C")
                .type(Foundling.FoundlingType.PAGE)
                .build();

        assertEquals("T", f.getTitle());
        assertEquals("C", f.getContent());
        assertEquals(Foundling.FoundlingType.PAGE, f.getType());
    }

    @Test
    void quarkusMockCanBeInstalledForFoundable() {
        // mock Foundable with Mockito
        Foundable mock = Mockito.mock(Foundable.class);
        Mockito.when(mock.toFoundling()).thenReturn(
                Foundling.builder().title("mock").content("c").type(Foundling.FoundlingType.USER).build()
        );

        Foundling result = mock.toFoundling();
        assertEquals("mock", result.getTitle());
    }
}
