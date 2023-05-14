package com.blitzar.bank.accounts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

public class TestObjectMapperFactory {

    @Primary
    @Singleton
    public ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
