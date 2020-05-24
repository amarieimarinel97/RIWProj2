package com.tuiasi.exception;

public class BadGatewayException extends Exception {
    public BadGatewayException(String errorMessage) {
        super(errorMessage);
    }
}