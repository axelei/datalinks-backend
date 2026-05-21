package net.krusher.datalinks.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorTypeTest {

    @Test
    void shouldContainExpectedValues() {
        // smoke-check a couple of enum values
        assertNotNull(ErrorType.SERVER_ERROR);
        assertEquals("PAGE_NOT_FOUND", ErrorType.PAGE_NOT_FOUND.name());
    }
}
