package com.demo.cqrs.rpc;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "_class")
public abstract class Response extends Traceable {
    /**
     * Mapping request id
     */
    protected String requestId;

    protected String msg;

    public boolean isSuccess() {
        return true;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
