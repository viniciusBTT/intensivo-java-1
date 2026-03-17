package com.intensivo.java.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnotacoesRestController.class)
class AnotacoesRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarValorDaQueryStringComRequestParam() throws Exception {
        mockMvc.perform(get("/api/exemplos/request-param").param("nome", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conceito").value("@RequestParam"))
                .andExpect(jsonPath("$.valorRecebido").value("Ana"));
    }

    @Test
    void deveRetornarValorDaUrlComPathVariable() throws Exception {
        mockMvc.perform(get("/api/exemplos/path-variable/Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conceito").value("@PathVariable"))
                .andExpect(jsonPath("$.valorRecebido").value("Ana"));
    }

    @Test
    void deveReceberJsonComRequestBody() throws Exception {
        String body = """
                {
                  "nome": "Joao",
                  "mensagem": "Aprendendo RequestBody"
                }
                """;

        mockMvc.perform(post("/api/exemplos/request-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conceito").value("@RequestBody"))
                .andExpect(jsonPath("$.nomeRecebido").value("Joao"))
                .andExpect(jsonPath("$.mensagemRecebida").value("Aprendendo RequestBody"));
    }
}
