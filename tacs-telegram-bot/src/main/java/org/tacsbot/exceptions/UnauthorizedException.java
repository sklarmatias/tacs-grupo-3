package org.tacsbot.exceptions;

import lombok.Getter;

@Getter
public class UnauthorizedException extends Exception{

    private final String sessionId;

    public UnauthorizedException(String sessionId) {
        super();
        this.sessionId = sessionId;
    }
}
