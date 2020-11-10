package com.demo.cqrs.exception;

public class RpcException extends Exception {
    private final int errorCode;

    public RpcException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
