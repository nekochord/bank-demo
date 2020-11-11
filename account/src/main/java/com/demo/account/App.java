package com.demo.account;

import com.demo.cqrs.command.account.TransferCmd;
import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.rpc.RpcFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
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

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Function<Message<Request>, Message<Response>> dispatcher(ApplicationContext applicationContext) {
        return new RpcFunctionManager(applicationContext).rpc();
    }

    @Bean
    public Supplier<Request> supplier() {
        LinkedList<Request> queue = new LinkedList<>();
        TransferCmd cmd = new TransferCmd();
        cmd.setFromAccountId(14L);
        cmd.setToAccountId(15L);
        cmd.setAmount(new BigDecimal(100));
        cmd.setReplyTo("result");
        cmd.setTrace("trace");
        queue.push(cmd);
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
