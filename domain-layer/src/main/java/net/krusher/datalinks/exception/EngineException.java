package net.krusher.datalinks.exception;

import lombok.Getter;

@Getter
public class EngineException extends RuntimeException {

    private final ErrorType errorType;

    public EngineException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public EngineException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public EngineException(ErrorType errorType, Throwable cause) {
        super(cause);
        this.errorType = errorType;
    }
}
