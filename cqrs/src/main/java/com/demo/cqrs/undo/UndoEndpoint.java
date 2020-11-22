package com.demo.cqrs.undo;

import com.demo.cqrs.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.ClassUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class UndoEndpoint {
    private final Map<Type, UndoConsumer<Undo>> consumerMap = new HashMap<>();
    private final UndoLogRepository undoLogRepository;
    private final PlatformTransactionManager transactionManager;

    public UndoEndpoint(UndoLogRepository undoLogRepository,
                        PlatformTransactionManager transactionManager,
                        ApplicationContext applicationContext) {
        this.undoLogRepository = undoLogRepository;
        this.transactionManager = transactionManager;
        for (UndoConsumer<Undo> consumer : applicationContext.getBeansOfType(UndoConsumer.class).values()) {
            Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(ClassUtils.getUserClass(consumer), UndoConsumer.class);

            if (generics != null) {
                consumerMap.put(generics[0], consumer);
                log.info("initialize UndoConsumer: {}", ClassUtils.getUserClass(consumer));
            }
        }
    }

    public Consumer<Message<UndoCommand>> endpoint() {
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

        TransactionStatus transaction = transactionManager.getTransaction(TransactionDefinition.withDefaults());
        try {
            if (undo == null) {
                throw new IllegalStateException("Undo is null for undoLog, requestId=" + requestId);
            }

            UndoConsumer<Undo> consumer = consumerMap.get(undo.getClass());

            if (consumer == null) {
                throw new IllegalStateException("Unsupported undo type, requestId=" + requestId);
            }

            consumer.consume(undo);
            undoLog.setStatus(UndoLogStatus.UNDONE);
        } catch (IllegalStateException e) {
            undoLog.setStatus(UndoLogStatus.ERROR);
            undoLog.setReason(e.getMessage());
        } catch (RpcException e) {
            undoLog.setStatus(UndoLogStatus.ERROR);
            undoLog.setCode(e.getCode());
            undoLog.setReason(e.getReason());
        } catch (Exception e) {
            undoLog.setStatus(UndoLogStatus.ERROR);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(outputStream, true));
            undoLog.setReason(outputStream.toString());
        } finally {
            undoLogRepository.save(undoLog);
            transactionManager.commit(transaction);
        }
    }
}
