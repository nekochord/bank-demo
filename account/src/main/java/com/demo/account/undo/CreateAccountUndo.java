package com.demo.account.undo;

import com.demo.cqrs.undo.Undo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountUndo implements Undo {
    private Long accountId;
}
