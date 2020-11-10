package com.demo.cqrs.exception;

public interface ErrorEnum {
    public int getCode();

    public String getReason();

    public default void raise() throws RpcException {
        throw exception();
    }

    public default RpcException exception() {
        return new RpcException(getCode(), getReason());
    }
}
