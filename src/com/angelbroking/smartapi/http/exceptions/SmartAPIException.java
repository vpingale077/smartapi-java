package com.angelbroking.smartapi.http.exceptions;

/**
 * This is the base exception class which has a publicly accessible message and code that
 * is received from Angel Connect api.
 */

public class SmartAPIException extends Throwable {

    // variables
    public String message;
    public int code;

    // constructor that sets the message
    public SmartAPIException(String message){
        this.message = message;
    }

    // constructor that sets the message and code
    public SmartAPIException(String message, int code){
        this.message = message;
        this.code = code;
    }
}
