package com.tuiasi.exception;


import lombok.Getter;

@Getter
public enum InternalErrorCodes {
    SUCCESS(0), ADD_DELAY(1), REMOVE_FROM_QUEUE(2), NOT_FOUND(4);
    private int code;

    InternalErrorCodes(int code) {
        this.code = code;
    }
}
