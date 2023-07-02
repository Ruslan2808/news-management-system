package ru.clevertec.authservice.integration;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15");

    @BeforeAll
    static void init() {
        container.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}
