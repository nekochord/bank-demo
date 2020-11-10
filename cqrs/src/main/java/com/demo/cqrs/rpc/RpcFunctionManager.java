package com.demo.cqrs.rpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class RpcFunctionManager {
    private static final String REPLY_DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private final Map<Class<?>, RpcFunction<Request, Response>> functionMap = new HashMap<>();

    public RpcFunctionManager(ApplicationContext applicationContext) {
        for (RpcFunction<Request, Response> function : applicationContext.getBeansOfType(RpcFunction.class).values()) {
            log.info("initialize function: {}", function.getClass());
            functionMap.put(function.type(), function);
        }
    }

    public Function<Message<Request>, Message<Response>> rpc() {
        return this::call;
    }

    private Message<Response> call(Message<Request> requestMessage) {
        Request request = requestMessage.getPayload();
        RpcFunction<Request, Response> function = functionMap.get(request.getClass());

        if (function == null) {
            Response errorResponse = new ErrorResponse("unsupported request type");
            return buildResponse(request, errorResponse);
        }

        try {
            Response response = function.handle(request);
            return buildResponse(request, response);
        } catch (Exception e) {
            Response errorResponse = new ErrorResponse(e.getMessage());
            return buildResponse(request, errorResponse);
        }
    }

    private static Message<Response> buildResponse(Request request, Response response) {
        return MessageBuilder.withPayload(response)
                .setHeader(REPLY_DESTINATION_HEADER, request.getReplyTo())
                .build();
    }
}
