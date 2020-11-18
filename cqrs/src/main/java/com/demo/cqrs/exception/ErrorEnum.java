package com.demo.cqrs.exception;

public interface ErrorEnum {
    int getCode();

    String getReason();

    default RpcException exception() {
        return new RpcException(getCode(), getReason());
    }
}
