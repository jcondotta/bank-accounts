package com.blitzar.bank.accounts.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import java.time.Clock;

@Factory
public class ClockFactory {

    @Bean
    public Clock systemUTCClock() {
        return Clock.systemUTC();
    }
}