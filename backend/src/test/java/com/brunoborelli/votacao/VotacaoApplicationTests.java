package com.brunoborelli.votacao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
class VotacaoApplicationTests {

	@Container
	static final PostgreSQLContainer bancoDeDados = new PostgreSQLContainer("postgres:17-alpine")
			.withDatabaseName("votacao_teste")
			.withUsername("votacao")
			.withPassword("votacao");

	@DynamicPropertySource
	static void configurarBancoDeDados(DynamicPropertyRegistry registro) {
		registro.add("spring.datasource.url", bancoDeDados::getJdbcUrl);
		registro.add("spring.datasource.username", bancoDeDados::getUsername);
		registro.add("spring.datasource.password", bancoDeDados::getPassword);
	}
	@Test
	void contextLoads() {
	}

}
