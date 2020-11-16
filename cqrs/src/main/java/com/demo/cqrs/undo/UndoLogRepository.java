package com.demo.cqrs.undo;

import java.util.Optional;

/**
 * Implement this interface to provide UndoLog repository
 */
public interface UndoLogRepository {

    /**
     * Acquire UndoLog, must implement this method atomically
     */
    public Optional<AbstractUndoLog> acquireFreeAndSetToLocked(String requestId);

    public void save(AbstractUndoLog abstractUndoLog);
}
