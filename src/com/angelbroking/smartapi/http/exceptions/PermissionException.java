package com.angelbroking.smartapi.http.exceptions;

/**
 * Represents permission denied exceptions for certain calls.
 * Default code is 403
 */
public class PermissionException extends SmartAPIException {
    public PermissionException(String message, int code){
        super(message, code);
    }
}
