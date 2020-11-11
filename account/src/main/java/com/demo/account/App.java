package com.demo.account;

import com.demo.cqrs.rpc.Request;
import com.demo.cqrs.rpc.Response;
import com.demo.cqrs.rpc.RpcFunctionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.Message;

import java.util.function.Function;

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

    
}
