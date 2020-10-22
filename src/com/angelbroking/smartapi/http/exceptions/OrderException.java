package com.angelbroking.smartapi.http.exceptions;

/**
 * Represents all order placement and manipulation errors.
 * Default code is 500.
 */

public class OrderException extends SmartAPIException {

    // initialize Order Exception and call base exception constructor
    public OrderException(String message, int code){
        super(message, code);
    }
}
