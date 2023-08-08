package com.blitzar.bank.accounts;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;

@Testcontainers
public interface MySQLTestContainer extends TestPropertyProvider {

    String mySQLImageName = "mysql:8.0";
    String databaseName = "integration-test-db";

    MySQLContainer<?> MYSQL_CONTAINER = (MySQLContainer) new MySQLContainer(mySQLImageName)
            .withDatabaseName(databaseName);

    @Override
    default Map<String, String> getProperties() {
        Startables.deepStart(MYSQL_CONTAINER).join();

        return getMySQLProperties();
    }

    default Map<String, String> getMySQLProperties() {
        return Map.of(
                "database.url", MYSQL_CONTAINER.getJdbcUrl(),
                "database.username", MYSQL_CONTAINER.getUsername(),
                "database.password", MYSQL_CONTAINER.getPassword(),
                "driverClassName", MYSQL_CONTAINER.getDriverClassName());
    }
}


