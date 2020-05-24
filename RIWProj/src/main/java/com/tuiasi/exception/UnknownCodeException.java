package com.tuiasi.exception;

public class UnknownCodeException extends Exception {
    public UnknownCodeException(String errorMessage) {
        super(errorMessage);
    }
}