package com.demo.cqrs.command;

import com.demo.cqrs.exception.RpcException;
import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ClassUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public class CommandEndpoint {
    private static final String REPLY_DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Map<Class<?>, CommandFunction<Request, Response>> functionMap = new HashMap<>();

    public CommandEndpoint(ApplicationContext applicationContext) {
        for (CommandFunction<Request, Response> function : applicationContext.getBeansOfType(CommandFunction.class).values()) {
            Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(ClassUtils.getUserClass(function), CommandFunction.class);

            if (generics != null) {
                functionMap.put(generics[0], function);
                log.info("initialize CommandFunction: {}", ClassUtils.getUserClass(function));
            }
        }
    }

    public Function<Message<Request>, Message<Response>> endpoint() {
        return this::call;
    }

    private Message<Response> call(Message<Request> requestMessage) {
        Request request = requestMessage.getPayload();
        CommandFunction<Request, Response> function = functionMap.get(request.getClass());

        if (function == null) {
            return buildErrorResponse(request, null, "unsupported request type");
        }

        Set<ConstraintViolation<Request>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            violations.forEach(violation ->
                    sb.append("[path=").append(violation.getPropertyPath()).append(",")
                            .append("error=").append(violation.getMessage()).append("]"));
            return buildErrorResponse(request, null, sb.toString());
        }

        try {
            Response response = function.execute(request);
            return buildResponse(request, response);
        } catch (RpcException rpcException) {
            return buildErrorResponse(request, rpcException.getCode(), rpcException.getReason());
        } catch (Exception e) {
            return buildErrorResponse(request, null, e.getMessage());
        }
    }

    private static Message<Response> buildErrorResponse(Request request, Integer code, String reason) {
        Response errorResponse = new Response();
        errorResponse.setSuccess(false);
        errorResponse.setCode(code);
        errorResponse.setReason(reason);
        return buildResponse(request, errorResponse);
    }

    private static Message<Response> buildResponse(Request request, Response response) {
        response.setRequestId(request.getId());
        response.setTrace(request.getTrace());
        return MessageBuilder.withPayload(response)
                .setHeader(REPLY_DESTINATION_HEADER, request.getReplyTo())
                .build();
    }
}
