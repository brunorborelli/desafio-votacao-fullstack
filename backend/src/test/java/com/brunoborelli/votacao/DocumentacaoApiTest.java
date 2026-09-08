package com.brunoborelli.votacao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentacaoApiTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveDisponibilizarEspecificacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info.title").value("API de Votação"))
            .andExpect(jsonPath("$.info.version").value("v1"))
            .andExpect(jsonPath("$['paths']['/api/v1/pautas']").exists())
            .andExpect(jsonPath("$['paths']['/api/v1/pautas/{pautaId}/votos']").exists())
            .andExpect(jsonPath("$['paths']['/api/v1/pautas/{pautaId}/resultado']").exists());
    }

    @Test
    void deveDisponibilizarInterfaceSwagger() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", containsString("/swagger-ui/index.html")));
    }
}