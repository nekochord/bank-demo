package com.demo.cqrs.undo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class UndoConsumerManager {
    private final Map<Type, UndoConsumer<Undo>> consumerMap = new HashMap<>();
    private final UndoLogRepository undoLogRepository;

    public UndoConsumerManager(UndoLogRepository undoLogRepository,
                               ApplicationContext applicationContext) {
        this.undoLogRepository = undoLogRepository;
        for (UndoConsumer<Undo> consumer : applicationContext.getBeansOfType(UndoConsumer.class).values()) {
            Arrays.stream(ClassUtils.getUserClass(consumer).getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .filter(type -> UndoConsumer.class.equals(type.getRawType()))
                    .findAny()
                    .ifPresent(type -> {
                        consumerMap.put(type.getActualTypeArguments()[0], consumer);
                        log.info("initialize UndoConsumer: {}", consumer.getClass());
                    });
        }
    }

    public Consumer<Message<UndoCommand>> undo() {
        return this::call;
    }

    private void call(Message<UndoCommand> message) {
        UndoCommand undoCommand = message.getPayload();

        final String requestId = undoCommand.getRequestId();
        if (requestId == null) {
            log.error("UndoCommand requestId is Null, message={}", message);
            return;
        }

        Optional<AbstractUndoLog> undoLogOptional = undoLogRepository.acquireFreeAndSetToLocked(requestId);
        if (undoLogOptional.isEmpty()) {
            return;
        }

        AbstractUndoLog undoLog = undoLogOptional.get();
        Undo undo = undoLog.getUndo();
        UndoConsumer<Undo> consumer = consumerMap.get(undo.getClass());

        if (consumer == null) {
            log.error("Unsupported Undo type, requestId={} type={}", requestId, undo.getClass());
            return;
        }

        try {
            consumer.consume(undo);
            undoLog.setStatus(UndoLogStatus.UNDO);
        } catch (Exception e) {
            log.error("Fail to undo, requestId={}", requestId, e);
            undoLog.setStatus(UndoLogStatus.ERROR);
        }
    }
}
