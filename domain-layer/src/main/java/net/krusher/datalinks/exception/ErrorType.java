package net.krusher.datalinks.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    SERVER_ERROR("server.error"),
    PERMISSIONS_ERROR("permissions.error"),
    ;

    private final String messageKey;

}
