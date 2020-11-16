package com.demo.account.entity;

import com.demo.cqrs.undo.AbstractUndoLog;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "undo_log", schema = "account_service")
public class UndoLog extends AbstractUndoLog {
//empty
}
