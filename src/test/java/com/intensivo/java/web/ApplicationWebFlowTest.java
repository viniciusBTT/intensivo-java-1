package com.intensivo.java.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.support.AbstractMySqlIntegrationTest;
import com.intensivo.java.support.StubCepLookupConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(StubCepLookupConfiguration.class)
class ApplicationWebFlowTest extends AbstractMySqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void deveRedirecionarNaoAutenticadoParaLogin() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void devePermitirLoginComUsuarioSemeadoNoBanco() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("admin123"))
                .andExpect(authenticated().withUsername("admin"));
    }

    @Test
    void deveBloquearExclusaoParaAtendente() throws Exception {
        mockMvc.perform(post("/clientes/1/excluir")
                        .with(user("atendente").roles("ATENDENTE"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarCepNormalizadoNoEndpointInterno() throws Exception {
        mockMvc.perform(get("/api/ceps/01001000").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep", is("01001000")))
                .andExpect(jsonPath("$.logradouro", is("Praca da Se")))
                .andExpect(jsonPath("$.cidade", is("Sao Paulo")));
    }

    @Test
    void deveCadastrarClientePfViaMvc() throws Exception {
        mockMvc.perform(post("/clientes/pf")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("nomeCompleto", "Joao da Silva")
                        .param("cpf", "12345678901")
                        .param("email", "joao@example.com")
                        .param("telefone", "11999998888")
                        .param("cep", "01001000")
                        .param("numero", "42")
                        .param("complemento", "Casa"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        org.assertj.core.api.Assertions.assertThat(clienteRepository.count()).isEqualTo(1L);
    }
}
