package com.demo.cqrs.rpc;

public interface RpcFunction<Req extends Request, Res extends Response> {
    /**
     * support which type
     */
    public Class<Req> type();

    /**
     * handle Request
     */
    public Res handle(Req request) throws Exception;
}
