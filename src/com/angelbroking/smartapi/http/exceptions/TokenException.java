package com.angelbroking.smartapi.http.exceptions;

/**
 * Denotes session is expired.
 */
public class TokenException extends SmartAPIException {
    public TokenException(String message, int code) {
        super(message, code);
    }
}
