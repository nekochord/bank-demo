package com.demo.cqrs.undo;

public interface UndoConsumer<U extends Undo> {
    /**
     * Consume Undo
     */
    void consume(U undo) throws Exception;
}
