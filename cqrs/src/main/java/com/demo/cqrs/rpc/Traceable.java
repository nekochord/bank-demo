package com.demo.cqrs.rpc;

public abstract class Traceable {
    protected String trace;

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }
}
