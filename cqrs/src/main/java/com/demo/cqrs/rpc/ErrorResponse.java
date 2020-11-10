package com.demo.cqrs.rpc;

public class ErrorResponse extends Response {

    public ErrorResponse() {
    }

    public ErrorResponse(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
