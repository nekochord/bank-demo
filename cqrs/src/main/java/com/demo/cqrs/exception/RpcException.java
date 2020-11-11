package com.demo.cqrs.exception;

public class RpcException extends Exception {
    private final int code;
    private final String reason;

    public RpcException(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
