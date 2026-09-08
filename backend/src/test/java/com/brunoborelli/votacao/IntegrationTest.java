package com.brunoborelli.votacao;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

abstract class IntegrationTest {

    static final PostgreSQLContainer bancoDeDados = new PostgreSQLContainer("postgres:17-alpine")
        .withDatabaseName("votacao_teste")
        .withUsername("votacao")
        .withPassword("votacao");

    static {
        bancoDeDados.start();
    }

    @DynamicPropertySource
    static void configurarBancoDeDados(DynamicPropertyRegistry registro) {
        registro.add("spring.datasource.url", bancoDeDados::getJdbcUrl);
        registro.add("spring.datasource.username", bancoDeDados::getUsername);
        registro.add("spring.datasource.password", bancoDeDados::getPassword);
    }
}