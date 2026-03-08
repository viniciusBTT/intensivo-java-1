package com.intensivo.java.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intensivo.java.dto.rest.ClienteCreateRequest;
import com.intensivo.java.dto.rest.ClienteRestType;
import com.intensivo.java.dto.rest.ContaCreateRequest;
import com.intensivo.java.dto.rest.ContaRestType;
import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.repository.ClientePessoaFisicaRepository;
import com.intensivo.java.repository.ClientePessoaJuridicaRepository;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.support.AbstractMySqlIntegrationTest;
import com.intensivo.java.support.StubCepLookupConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(StubCepLookupConfiguration.class)
class ApplicationRestFlowTest extends AbstractMySqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientePessoaFisicaRepository clientePessoaFisicaRepository;

    @Autowired
    private ClientePessoaJuridicaRepository clientePessoaJuridicaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void deveRedirecionarApiNaoAutenticadaParaLogin() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveListarClientesViaRest() throws Exception {
        clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Joao da Silva"));
        clientePessoaJuridicaRepository.saveAndFlush(clientePj("12345678000199", "Empresa XPTO LTDA", "XPTO"));

        mockMvc.perform(get("/api/clientes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tipo", is("PJ")))
                .andExpect(jsonPath("$[0].nomeFantasia", is("XPTO")))
                .andExpect(jsonPath("$[1].tipo", is("PF")))
                .andExpect(jsonPath("$[1].nomeCompleto", is("Joao da Silva")));
    }

    @Test
    void deveBuscarClientePorIdViaRest() throws Exception {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Maria Souza"));

        mockMvc.perform(get("/api/clientes/{id}", cliente.getId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cliente.getId().intValue())))
                .andExpect(jsonPath("$.tipo", is("PF")))
                .andExpect(jsonPath("$.nomeExibicao", is("Maria Souza")))
                .andExpect(jsonPath("$.documento", is("12345678901")))
                .andExpect(jsonPath("$.endereco.cidade", is("Sao Paulo")));
    }

    @Test
    void deveCriarClientePfViaRest() throws Exception {
        ClienteCreateRequest request = new ClienteCreateRequest();
        request.setTipo(ClienteRestType.PF);
        request.setNomeCompleto("Carla Dias");
        request.setCpf("12345678901");
        request.setEmail("carla@example.com");
        request.setTelefone("11999998888");
        request.setCep("01001000");
        request.setNumero("42");
        request.setComplemento("Casa");

        mockMvc.perform(post("/api/clientes")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/clientes/")))
                .andExpect(jsonPath("$.tipo", is("PF")))
                .andExpect(jsonPath("$.nomeCompleto", is("Carla Dias")))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.endereco.logradouro", is("Praca da Se")));

        org.assertj.core.api.Assertions.assertThat(clienteRepository.count()).isEqualTo(1L);
    }

    @Test
    void deveCriarClientePjViaRest() throws Exception {
        ClienteCreateRequest request = new ClienteCreateRequest();
        request.setTipo(ClienteRestType.PJ);
        request.setRazaoSocial("Empresa XPTO LTDA");
        request.setNomeFantasia("XPTO");
        request.setCnpj("12345678000199");
        request.setEmail("empresa@example.com");
        request.setTelefone("1133334444");
        request.setCep("01001000");
        request.setNumero("10");
        request.setComplemento("Sala 2");

        mockMvc.perform(post("/api/clientes")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", is("PJ")))
                .andExpect(jsonPath("$.razaoSocial", is("Empresa XPTO LTDA")))
                .andExpect(jsonPath("$.nomeFantasia", is("XPTO")))
                .andExpect(jsonPath("$.cnpj", is("12345678000199")));

        org.assertj.core.api.Assertions.assertThat(clienteRepository.count()).isEqualTo(1L);
    }

    @Test
    void deveRetornar400AoCriarClienteRestComCamposEspecificosInvalidos() throws Exception {
        ClienteCreateRequest request = new ClienteCreateRequest();
        request.setTipo(ClienteRestType.PF);
        request.setEmail("carla@example.com");
        request.setTelefone("11999998888");
        request.setCep("01001000");
        request.setNumero("42");

        mockMvc.perform(post("/api/clientes")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados invalidos.")))
                .andExpect(jsonPath("$.fieldErrors.nomeCompleto", is("Informe o nome completo.")))
                .andExpect(jsonPath("$.fieldErrors.cpf", is("Informe o CPF.")));
    }

    @Test
    void deveRetornar400AoCriarClienteRestComCpfDuplicado() throws Exception {
        clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Cliente Base"));

        ClienteCreateRequest request = new ClienteCreateRequest();
        request.setTipo(ClienteRestType.PF);
        request.setNomeCompleto("Outro Cliente");
        request.setCpf("12345678901");
        request.setEmail("outro@example.com");
        request.setTelefone("11999998888");
        request.setCep("01001000");
        request.setNumero("42");

        mockMvc.perform(post("/api/clientes")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("CPF")));
    }

    @Test
    void deveRetornar404AoBuscarClienteRestInexistente() throws Exception {
        mockMvc.perform(get("/api/clientes/99999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente nao encontrado.")));
    }

    @Test
    void deveListarContasViaRest() throws Exception {
        ClientePessoaFisica clientePf = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Joao da Silva"));
        ClientePessoaJuridica clientePj = clientePessoaJuridicaRepository.saveAndFlush(
                clientePj("12345678000199", "Empresa XPTO LTDA", "XPTO"));
        contaRepository.saveAndFlush(contaCorrente("00000001", clientePf));
        contaRepository.saveAndFlush(contaJuridica("00000002", clientePj));

        mockMvc.perform(get("/api/contas").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tipo", is("JURIDICA")))
                .andExpect(jsonPath("$[0].clienteNome", is("XPTO")))
                .andExpect(jsonPath("$[1].tipo", is("CORRENTE")))
                .andExpect(jsonPath("$[1].clienteNome", is("Joao da Silva")));
    }

    @Test
    void deveBuscarContaPorIdViaRest() throws Exception {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Cliente PF"));
        ContaCorrente conta = contaRepository.saveAndFlush(contaCorrente("00000001", cliente));

        mockMvc.perform(get("/api/contas/{id}", conta.getId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(conta.getId().intValue())))
                .andExpect(jsonPath("$.tipo", is("CORRENTE")))
                .andExpect(jsonPath("$.numero", is("00000001")))
                .andExpect(jsonPath("$.clienteId", is(cliente.getId().intValue())))
                .andExpect(jsonPath("$.limiteChequeEspecial", is(500.00)));
    }

    @Test
    void deveCriarContaCorrenteViaRest() throws Exception {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Cliente PF"));

        ContaCreateRequest request = new ContaCreateRequest();
        request.setTipo(ContaRestType.CORRENTE);
        request.setClienteId(cliente.getId());
        request.setSaldoInicial(new BigDecimal("1000.00"));
        request.setLimiteChequeEspecial(new BigDecimal("250.00"));

        mockMvc.perform(post("/api/contas")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/contas/")))
                .andExpect(jsonPath("$.tipo", is("CORRENTE")))
                .andExpect(jsonPath("$.status", is("ATIVA")))
                .andExpect(jsonPath("$.clienteNome", is("Cliente PF")))
                .andExpect(jsonPath("$.limiteChequeEspecial", is(250.00)));

        org.assertj.core.api.Assertions.assertThat(contaRepository.count()).isEqualTo(1L);
    }

    @Test
    void deveCriarContaJuridicaViaRest() throws Exception {
        ClientePessoaJuridica cliente = clientePessoaJuridicaRepository.saveAndFlush(
                clientePj("12345678000199", "Empresa XPTO LTDA", "XPTO"));

        ContaCreateRequest request = new ContaCreateRequest();
        request.setTipo(ContaRestType.JURIDICA);
        request.setClienteId(cliente.getId());
        request.setSaldoInicial(new BigDecimal("3000.00"));
        request.setStatus(ContaStatus.BLOQUEADA);
        request.setTaxaPacoteMensal(new BigDecimal("89.90"));
        request.setResponsavelFinanceiro("Ana Financeira");

        mockMvc.perform(post("/api/contas")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", is("JURIDICA")))
                .andExpect(jsonPath("$.status", is("BLOQUEADA")))
                .andExpect(jsonPath("$.taxaPacoteMensal", is(89.90)))
                .andExpect(jsonPath("$.responsavelFinanceiro", is("Ana Financeira")));
    }

    @Test
    void deveRetornar400AoCriarContaRestComCamposEspecificosInvalidos() throws Exception {
        ClientePessoaJuridica cliente = clientePessoaJuridicaRepository.saveAndFlush(
                clientePj("12345678000199", "Empresa XPTO LTDA", "XPTO"));

        ContaCreateRequest request = new ContaCreateRequest();
        request.setTipo(ContaRestType.JURIDICA);
        request.setClienteId(cliente.getId());
        request.setSaldoInicial(new BigDecimal("3000.00"));

        mockMvc.perform(post("/api/contas")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Dados invalidos.")))
                .andExpect(jsonPath("$.fieldErrors.taxaPacoteMensal", is("Informe a tarifa mensal.")))
                .andExpect(jsonPath("$.fieldErrors.responsavelFinanceiro", is("Informe o responsavel financeiro.")));
    }

    @Test
    void deveRetornar400AoCriarContaRestIncompativelComCliente() throws Exception {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901", "Cliente PF"));

        ContaCreateRequest request = new ContaCreateRequest();
        request.setTipo(ContaRestType.JURIDICA);
        request.setClienteId(cliente.getId());
        request.setSaldoInicial(new BigDecimal("3000.00"));
        request.setTaxaPacoteMensal(new BigDecimal("89.90"));
        request.setResponsavelFinanceiro("Ana Financeira");

        mockMvc.perform(post("/api/contas")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("compativel")));
    }

    @Test
    void deveRetornar404AoBuscarContaRestInexistente() throws Exception {
        mockMvc.perform(get("/api/contas/99999").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Conta nao encontrada.")));
    }

    private ClientePessoaFisica clientePf(String cpf, String nome) {
        ClientePessoaFisica cliente = new ClientePessoaFisica();
        cliente.setNomeCompleto(nome);
        cliente.setCpf(cpf);
        cliente.setEmail(nome.toLowerCase().replace(" ", ".") + "@example.com");
        cliente.setTelefone("11999998888");
        cliente.setEndereco(enderecoPadrao("100"));
        return cliente;
    }

    private ClientePessoaJuridica clientePj(String cnpj, String razaoSocial, String nomeFantasia) {
        ClientePessoaJuridica cliente = new ClientePessoaJuridica();
        cliente.setRazaoSocial(razaoSocial);
        cliente.setNomeFantasia(nomeFantasia);
        cliente.setCnpj(cnpj);
        cliente.setEmail("empresa." + nomeFantasia.toLowerCase() + "@example.com");
        cliente.setTelefone("1133334444");
        cliente.setEndereco(enderecoPadrao("200"));
        return cliente;
    }

    private ContaCorrente contaCorrente(String numero, ClientePessoaFisica cliente) {
        ContaCorrente conta = new ContaCorrente();
        conta.setAgencia("0001");
        conta.setNumero(numero);
        conta.setSaldoInicial(new BigDecimal("1000.00"));
        conta.setStatus(ContaStatus.ATIVA);
        conta.setLimiteChequeEspecial(new BigDecimal("500.00"));
        conta.setCliente(cliente);
        return conta;
    }

    private ContaJuridica contaJuridica(String numero, ClientePessoaJuridica cliente) {
        ContaJuridica conta = new ContaJuridica();
        conta.setAgencia("0001");
        conta.setNumero(numero);
        conta.setSaldoInicial(new BigDecimal("3000.00"));
        conta.setStatus(ContaStatus.ATIVA);
        conta.setTaxaPacoteMensal(new BigDecimal("89.90"));
        conta.setResponsavelFinanceiro("Ana Financeira");
        conta.setCliente(cliente);
        return conta;
    }

    private Endereco enderecoPadrao(String numero) {
        return Endereco.builder()
                .cep("01001000")
                .logradouro("Praca da Se")
                .numero(numero)
                .bairro("Se")
                .cidade("Sao Paulo")
                .uf("SP")
                .build();
    }
}
