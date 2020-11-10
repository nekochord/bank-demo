package com.demo.cqrs.command.account;

import com.demo.cqrs.command.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountCmd extends Command {
    private String name;
}
