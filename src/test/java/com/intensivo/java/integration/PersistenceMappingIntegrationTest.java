package com.intensivo.java.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.repository.ClientePessoaFisicaRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.support.AbstractMySqlIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class PersistenceMappingIntegrationTest extends AbstractMySqlIntegrationTest {

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
    void devePersistirHerancaDeClienteEConta() {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901"));

        ContaCorrente conta = new ContaCorrente();
        conta.setAgencia("0001");
        conta.setNumero("12345678");
        conta.setSaldoInicial(new BigDecimal("1000.00"));
        conta.setStatus(ContaStatus.ATIVA);
        conta.setLimiteChequeEspecial(new BigDecimal("500.00"));
        conta.setCliente(cliente);

        contaRepository.saveAndFlush(conta);

        var contas = contaRepository.findAllByOrderByIdDesc();
        assertThat(contas).hasSize(1);
        assertThat(contas.getFirst()).isInstanceOf(ContaCorrente.class);
        assertThat(contas.getFirst().getCliente()).isInstanceOf(ClientePessoaFisica.class);
    }

    @Test
    void deveGarantirUnicidadeDeCpf() {
        clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901"));

        assertThatThrownBy(() -> clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveGarantirUnicidadeDoNumeroDaConta() {
        ClientePessoaFisica cliente = clientePessoaFisicaRepository.saveAndFlush(clientePf("12345678901"));
        contaRepository.saveAndFlush(conta("99999999", cliente));

        assertThatThrownBy(() -> contaRepository.saveAndFlush(conta("99999999", cliente)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private ClientePessoaFisica clientePf(String cpf) {
        ClientePessoaFisica cliente = new ClientePessoaFisica();
        cliente.setNomeCompleto("Cliente Teste");
        cliente.setCpf(cpf);
        cliente.setEmail("cliente@example.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco(Endereco.builder()
                .cep("01001000")
                .logradouro("Praca da Se")
                .numero("100")
                .bairro("Se")
                .cidade("Sao Paulo")
                .uf("SP")
                .build());
        return cliente;
    }

    private ContaCorrente conta(String numero, ClientePessoaFisica cliente) {
        ContaCorrente conta = new ContaCorrente();
        conta.setAgencia("0001");
        conta.setNumero(numero);
        conta.setSaldoInicial(new BigDecimal("100.00"));
        conta.setStatus(ContaStatus.ATIVA);
        conta.setLimiteChequeEspecial(new BigDecimal("50.00"));
        conta.setCliente(cliente);
        return conta;
    }
}
