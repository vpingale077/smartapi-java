package com.angelbroking.smartapi.http.exceptions;

/**
 * Represents user input errors such as missing and invalid parameters.
 * Default code is 400.
 */
public class InputException extends SmartAPIException {
    // initialize and call base exception constructor
    public InputException(String message, int code){
        super(message, code);
    }
}

