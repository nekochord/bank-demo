package com.demo.merchant;

import com.demo.cqrs.command.CommandEndpoint;
import com.demo.cqrs.query.QueryEndpoint;
import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.undo.UndoCommand;
import com.demo.cqrs.undo.UndoEndpoint;
import com.demo.cqrs.undo.UndoLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.Message;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@EnableJpaAuditing
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public Function<Message<Request>, Message<Response>> command(ApplicationContext applicationContext) {
        return new CommandEndpoint(applicationContext).endpoint();
    }

    @Bean
    public Function<Message<Request>, Message<Response>> query(ApplicationContext applicationContext) {
        return new QueryEndpoint(applicationContext).endpoint();
    }

    @Bean
    public Consumer<Message<UndoCommand>> undo(UndoLogRepository undoLogRepository,
                                               PlatformTransactionManager platformTransactionManager,
                                               ApplicationContext applicationContext) {
        return new UndoEndpoint(undoLogRepository, platformTransactionManager, applicationContext).endpoint();
    }

    @Bean
    public Supplier<Request> supplier() {
        LinkedList<Request> queue = new LinkedList<>();

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setRequestId("bcae27db-cdd2-4184-867d-2ea48447667b");

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
