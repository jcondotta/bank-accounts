package com.blitzar.bank.accounts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class JacksonObjectMapperFactory {

    @Singleton
    public ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
