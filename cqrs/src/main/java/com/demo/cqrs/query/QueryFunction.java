package com.demo.cqrs.query;

import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;

public interface QueryFunction<Req extends Request, Res extends Response> {
    Res query(Req request) throws Exception;
}
