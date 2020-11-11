package com.demo.cqrs.rpc;

import com.demo.cqrs.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class RpcFunctionManager {
    private static final String REPLY_DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private final Map<Type, RpcFunction<Request, Response>> functionMap = new HashMap<>();

    public RpcFunctionManager(ApplicationContext applicationContext) {
        for (RpcFunction<Request, Response> function : applicationContext.getBeansOfType(RpcFunction.class).values()) {
            Arrays.stream(ClassUtils.getUserClass(function).getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .filter(type -> RpcFunction.class.equals(type.getRawType()))
                    .findAny()
                    .ifPresent(type -> {
                        functionMap.put(type.getActualTypeArguments()[0], function);
                        log.info("initialize RpcFunction: {}", function.getClass());
                    });
        }
    }

    public Function<Message<Request>, Message<Response>> rpc() {
        return this::call;
    }

    private Message<Response> call(Message<Request> requestMessage) {
        Request request = requestMessage.getPayload();
        RpcFunction<Request, Response> function = functionMap.get(request.getClass());

        if (function == null) {
            Response errorResponse = new Response();
            errorResponse.setSuccess(false);
            errorResponse.setReason("unsupported request type");
            return buildResponse(request, errorResponse);
        }

        try {
            Response response = function.handle(request);
            return buildResponse(request, response);
        } catch (RpcException rpcException) {
            Response errorResponse = new Response();
            errorResponse.setSuccess(false);
            errorResponse.setCode(rpcException.getCode());
            errorResponse.setReason(rpcException.getReason());
            return buildResponse(request, errorResponse);
        } catch (Exception e) {
            Response errorResponse = new Response();
            errorResponse.setSuccess(false);
            errorResponse.setReason(e.getMessage());
            return buildResponse(request, errorResponse);
        }
    }

    private static Message<Response> buildResponse(Request request, Response response) {
        return MessageBuilder.withPayload(response)
                .setHeader(REPLY_DESTINATION_HEADER, request.getReplyTo())
                .build();
    }
}
