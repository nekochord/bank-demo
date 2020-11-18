package com.demo.cqrs.command;

import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;

public interface CommandFunction<Req extends Request, Res extends Response> {
    Res execute(Req request) throws Exception;
}
