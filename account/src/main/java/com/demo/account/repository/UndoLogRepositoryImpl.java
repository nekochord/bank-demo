package com.demo.account.repository;

import com.demo.account.entity.UndoLog;
import com.demo.cqrs.undo.AbstractUndoLog;
import com.demo.cqrs.undo.UndoLogRepository;
import com.demo.cqrs.undo.UndoLogStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class UndoLogRepositoryImpl implements UndoLogRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Optional<AbstractUndoLog> acquireFreeAndSetToLocked(String requestId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UndoLog> q = cb.createQuery(UndoLog.class);
        Root<UndoLog> undoLogRoot = q.from(UndoLog.class);
        q.select(undoLogRoot).where(cb.and(
                cb.equal(undoLogRoot.get("requestId"), requestId),
                cb.equal(undoLogRoot.get("status"), UndoLogStatus.FREE)
        ));

        UndoLog undoLog = entityManager.createQuery(q)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();

        if (undoLog != null) {
            undoLog.setStatus(UndoLogStatus.LOCKED);
        }
        return Optional.ofNullable(undoLog);
    }

    @Override
    @Transactional
    public void save(AbstractUndoLog abstractUndoLog) {
        entityManager.merge(abstractUndoLog);
    }
}
