package com.demo.account;

import com.demo.cqrs.command.CommandEndpoint;
import com.demo.cqrs.command.account.CreateAccountCmd;
import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.rpc.Traceable;
import com.demo.cqrs.undo.UndoCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.Message;

import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@EnableJpaAuditing
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Function<Message<Request>, Message<Response>> command(ApplicationContext applicationContext) {
        return new CommandEndpoint(applicationContext).endpoint();
    }

    @Bean
    public Supplier<Traceable> supplier() {
        LinkedList<Traceable> queue = new LinkedList<>();

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setRequestId("29e8cf50-0b27-46fe-abc8-577377f381c6");

        CreateAccountCmd cmd = new CreateAccountCmd();
        cmd.setId(UUID.randomUUID().toString());
        cmd.setName("AAAG");
        cmd.setReplyTo("result");
        cmd.setTrace("trace");
        queue.push(undoCommand);
        return queue::poll;
    }

    @Bean
    public Consumer<Response> consumer() {
        return (res) -> {
            try {
                System.out.println(new ObjectMapper().writeValueAsString(res));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
