package net.krusher.datalinks.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class EngineExceptionTest {

    @Test
    void messageAndErrorTypeAreExposed() {
        EngineException ex = new EngineException(ErrorType.USER_NOT_FOUND, "no user");
        assertEquals(ErrorType.USER_NOT_FOUND, ex.getErrorType());
        assertEquals("no user", ex.getMessage());
    }

    @Test
    void causeIsPreserved() {
        Throwable cause = new RuntimeException("cause");
        EngineException ex = new EngineException(ErrorType.SERVER_ERROR, "fail", cause);
        assertSame(cause, ex.getCause());
    }
}
