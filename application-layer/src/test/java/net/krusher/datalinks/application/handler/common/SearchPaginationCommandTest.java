package net.krusher.datalinks.application.handler.common;

import net.krusher.datalinks.domain.exception.EngineException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchPaginationCommandTest {

    @Test
    void validSearchPaginationDoesNotThrow() {
        SearchPaginationCommand cmd = SearchPaginationCommand.builder().page(0).pageSize(10).query("x").build();
        assertDoesNotThrow(cmd::validate);
    }

    @Test
    void negativePageThrows() {
        SearchPaginationCommand cmd = SearchPaginationCommand.builder().page(-1).pageSize(10).query("x").build();
        EngineException ex = assertThrows(EngineException.class, cmd::validate);
        assertTrue(ex.getMessage().contains("Page number must be positive"));
    }

    @Test
    void invalidPageSizeThrows() {
        SearchPaginationCommand cmd = SearchPaginationCommand.builder().page(0).pageSize(7).query("x").build();
        EngineException ex = assertThrows(EngineException.class, cmd::validate);
        assertTrue(ex.getMessage().contains("Page size must be one of"));
    }
}
