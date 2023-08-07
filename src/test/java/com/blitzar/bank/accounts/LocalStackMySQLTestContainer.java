package com.blitzar.bank.accounts;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public interface LocalStackMySQLTestContainer extends MySQLTestContainer, AWSSQSTestContainer {

    @Override
    default Map<String, String> getProperties() {
        Startables.deepStart(LOCALSTACK_CONTAINER, MYSQL_CONTAINER).join();

        try {
            LOCALSTACK_CONTAINER.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "bank-account-application");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> mySQLProperties = getMySQLProperties();
        Map<String, String> sqsProperties = getSQSProperties();

        return Stream.of(mySQLProperties, sqsProperties)
                .flatMap(property -> property.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}


