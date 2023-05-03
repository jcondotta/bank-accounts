package com.blitzar.bank.accounts.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
public class TestTimeConfiguration {

    @Primary
    @Bean("testFixedInstantUTC")
    public Clock testFixedInstantUTC(){
        return Clock.fixed(
                Instant.parse("2018-06-24T12:45:01Z"),
                ZoneOffset.UTC);
    }
}
