package com.demo.cqrs.undo;

public enum UndoLogStatus {
    /**
     * waiting for UndoCommand
     */
    FREE,
    /**
     * Acquired by UndoConsumer process
     */
    LOCKED,
    /**
     * Already undone
     */
    UNDO,
    /**
     * Error and should not happen
     */
    ERROR
}
