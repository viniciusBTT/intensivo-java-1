package com.intensivo.java.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.dto.form.ContaCorrenteForm;
import com.intensivo.java.dto.form.ContaJuridicaForm;
import com.intensivo.java.exception.ContaClienteIncompativelException;
import com.intensivo.java.repository.ClientePessoaFisicaRepository;
import com.intensivo.java.repository.ClientePessoaJuridicaRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.support.AbstractMySqlIntegrationTest;
import com.intensivo.java.support.StubCepLookupConfiguration;
import java.math.BigDecimal;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(StubCepLookupConfiguration.class)
class ContaServiceIntegrationTest extends AbstractMySqlIntegrationTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClientePessoaFisicaRepository clientePessoaFisicaRepository;

    @Autowired
    private ClientePessoaJuridicaRepository clientePessoaJuridicaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        clientePessoaFisicaRepository.deleteAll();
        clientePessoaJuridicaRepository.deleteAll();
    }

    @Test
    void deveImpedirCriacaoDeContaJuridicaParaClientePf() {
        ClientePessoaFisica clientePf = clientePessoaFisicaRepository.saveAndFlush(clientePf());
        ContaJuridicaForm form = new ContaJuridicaForm();
        form.setClienteId(clientePf.getId());
        form.setSaldoInicial(new BigDecimal("1000.00"));
        form.setTaxaPacoteMensal(new BigDecimal("35.00"));
        form.setResponsavelFinanceiro("Financeiro");

        assertThatThrownBy(() -> contaService.criarContaJuridica(form))
                .isInstanceOf(ContaClienteIncompativelException.class);
    }

    @Test
    void deveGerarNumeroDeContaUnico() {
        ClientePessoaFisica clientePf = clientePessoaFisicaRepository.saveAndFlush(clientePf());

        IntStream.range(0, 10).forEach(index -> {
            ContaCorrenteForm form = new ContaCorrenteForm();
            form.setClienteId(clientePf.getId());
            form.setSaldoInicial(new BigDecimal("100.00"));
            form.setLimiteChequeEspecial(new BigDecimal("50.00"));
            contaService.criarContaCorrente(form);
        });

        assertThat(contaRepository.findAll())
                .extracting("numero")
                .doesNotHaveDuplicates();
    }

    @Test
    void deveCriarContaJuridicaParaClientePj() {
        ClientePessoaJuridica clientePj = clientePessoaJuridicaRepository.saveAndFlush(clientePj());

        ContaJuridicaForm form = new ContaJuridicaForm();
        form.setClienteId(clientePj.getId());
        form.setSaldoInicial(new BigDecimal("3000.00"));
        form.setTaxaPacoteMensal(new BigDecimal("89.90"));
        form.setResponsavelFinanceiro("Ana Financeira");
        contaService.criarContaJuridica(form);

        assertThat(contaRepository.findAll()).hasSize(1);
        assertThat(contaRepository.findAll().getFirst().getCliente().getId()).isEqualTo(clientePj.getId());
    }

    private ClientePessoaFisica clientePf() {
        ClientePessoaFisica cliente = new ClientePessoaFisica();
        cliente.setNomeCompleto("Carlos Pereira");
        cliente.setCpf("12345678901");
        cliente.setEmail("carlos@example.com");
        cliente.setTelefone("11999990000");
        cliente.setEndereco(enderecoPadrao());
        return cliente;
    }

    private ClientePessoaJuridica clientePj() {
        ClientePessoaJuridica cliente = new ClientePessoaJuridica();
        cliente.setRazaoSocial("Empresa XPTO LTDA");
        cliente.setNomeFantasia("XPTO");
        cliente.setCnpj("12345678000199");
        cliente.setEmail("empresa@example.com");
        cliente.setTelefone("1133334444");
        cliente.setEndereco(enderecoPadrao());
        return cliente;
    }

    private Endereco enderecoPadrao() {
        return Endereco.builder()
                .cep("01001000")
                .logradouro("Praca da Se")
                .numero("1")
                .bairro("Se")
                .cidade("Sao Paulo")
                .uf("SP")
                .build();
    }
}
