package com.blitzar.bank.accounts.config;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.time.Clock;

@Factory
public class ClockConfiguration {

    @Singleton
    public Clock currentInstantUTC(){
        return Clock.systemUTC();
    }
}