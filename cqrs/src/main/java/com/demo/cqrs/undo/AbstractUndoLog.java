package com.demo.cqrs.undo;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * Extend this class to implement UndoLog entity
 */
@MappedSuperclass
public abstract class AbstractUndoLog {
    @Id
    protected String requestId;
    @CreatedDate
    protected Date createdDate;
    @LastModifiedDate
    protected Date lastModifiedDate;
    @Enumerated(EnumType.STRING)
    protected UndoLogStatus status;
    @Convert(converter = UndoAttributeConverter.class)
    protected Undo undo;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public UndoLogStatus getStatus() {
        return status;
    }

    public void setStatus(UndoLogStatus status) {
        this.status = status;
    }

    public Undo getUndo() {
        return undo;
    }

    public void setUndo(Undo undo) {
        this.undo = undo;
    }
}
