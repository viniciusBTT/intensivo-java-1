package com.intensivo.java.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.intensivo.java.dto.form.ClientePessoaFisicaForm;
import com.intensivo.java.exception.DuplicateDocumentException;
import com.intensivo.java.repository.ClientePessoaFisicaRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.support.AbstractMySqlIntegrationTest;
import com.intensivo.java.support.StubCepLookupConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(StubCepLookupConfiguration.class)
class ClienteServiceIntegrationTest extends AbstractMySqlIntegrationTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClientePessoaFisicaRepository clientePessoaFisicaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        clientePessoaFisicaRepository.deleteAll();
    }

    @Test
    void deveImpedirCpfDuplicadoAoCriarClientePf() {
        clienteService.criarPessoaFisica(form("123.456.789-01"));

        assertThatThrownBy(() -> clienteService.criarPessoaFisica(form("12345678901")))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessageContaining("CPF");
    }

    @Test
    void devePersistirEnderecoCanonicoAoCriarClientePf() {
        clienteService.criarPessoaFisica(form("11122233344"));

        var cliente = clientePessoaFisicaRepository.findAll().getFirst();
        assertThat(cliente.getEndereco().getCep()).isEqualTo("01001000");
        assertThat(cliente.getEndereco().getLogradouro()).isEqualTo("Praca da Se");
        assertThat(cliente.getEndereco().getCidade()).isEqualTo("Sao Paulo");
        assertThat(cliente.getEndereco().getNumero()).isEqualTo("120");
    }

    private ClientePessoaFisicaForm form(String cpf) {
        ClientePessoaFisicaForm form = new ClientePessoaFisicaForm();
        form.setNomeCompleto("Maria da Silva");
        form.setCpf(cpf);
        form.setEmail("maria@example.com");
        form.setTelefone("(11) 99999-9999");
        form.setCep("01001-000");
        form.setNumero("120");
        form.setComplemento("Sala 1");
        return form;
    }
}
