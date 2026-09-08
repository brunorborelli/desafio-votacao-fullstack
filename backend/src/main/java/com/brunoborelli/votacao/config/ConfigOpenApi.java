package com.brunoborelli.votacao.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Votação",
        version = "v1",
        description = "API REST para gerenciamento de pautas e sessões de votação"
    )
)
public class ConfigOpenApi {
}