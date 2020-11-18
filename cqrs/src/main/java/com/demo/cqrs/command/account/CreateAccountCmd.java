package com.demo.cqrs.command.account;

import com.demo.cqrs.rpc.Request;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateAccountCmd extends Request {
    @NotBlank
    private String name;
}
