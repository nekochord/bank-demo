package com.demo.cqrs.undo;

import java.util.Optional;

/**
 * Implement this interface to provide UndoLog repository
 */
public interface UndoLogRepository {

    /**
     * Acquire UndoLog, must implement this method atomically
     */
    Optional<AbstractUndoLog> acquireFreeAndSetToLocked(String requestId);

    void save(AbstractUndoLog abstractUndoLog);
}
