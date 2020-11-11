package com.demo.cqrs.rpc;

public interface RpcFunction<Req extends Request, Res extends Response> {
    /**
     * handle Request
     */
    public Res handle(Req request) throws Exception;
}
