package net.krusher.datalinks.application.handler.common;

import net.krusher.datalinks.domain.exception.EngineException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationCommandTest {

    @Test
    void validPaginationDoesNotThrow() {
        PaginationCommand cmd = PaginationCommand.builder().page(0).pageSize(10).build();
        assertDoesNotThrow(cmd::validate);
    }

    @Test
    void negativePageThrows() {
        PaginationCommand cmd = PaginationCommand.builder().page(-1).pageSize(10).build();
        EngineException ex = assertThrows(EngineException.class, cmd::validate);
        assertTrue(ex.getMessage().contains("Page number must be positive"));
    }

    @Test
    void invalidPageSizeThrows() {
        PaginationCommand cmd = PaginationCommand.builder().page(0).pageSize(7).build();
        EngineException ex = assertThrows(EngineException.class, cmd::validate);
        assertTrue(ex.getMessage().contains("Page size must be one of"));
    }
}
