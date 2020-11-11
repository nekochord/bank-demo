package com.demo.cqrs.exception;

public interface ErrorEnum {
    public int getCode();

    public String getReason();

    public default RpcException exception() {
        return new RpcException(getCode(), getReason());
    }
}
