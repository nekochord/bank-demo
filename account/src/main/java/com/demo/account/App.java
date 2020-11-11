package com.demo.account;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.Message;

import com.demo.cqrs.command.account.DepositCmd;
import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.rpc.RpcFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        DepositCmd depositCmd = new DepositCmd();
        depositCmd.setAccountId(2L);
        depositCmd.setAmount(new BigDecimal(100));
        depositCmd.setTrace("trace");
        depositCmd.setReplyTo("result");
        depositCmd.setId("id");
        queue.push(depositCmd);
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
