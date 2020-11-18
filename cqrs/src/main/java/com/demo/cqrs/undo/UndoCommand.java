package com.demo.cqrs.undo;

import com.demo.cqrs.rpc.Request;

public class UndoCommand extends Request {
    protected String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
