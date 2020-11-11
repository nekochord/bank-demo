package com.demo.cqrs.command.account;

import com.demo.cqrs.command.Command;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateAccountCmd extends Command {
    @NotBlank
    private String name;
}
