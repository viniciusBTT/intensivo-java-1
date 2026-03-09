package com.intensivo.java.service.contas.impl;

import com.intensivo.java.dto.form.contas.ContaCorrenteForm;
import com.intensivo.java.dto.form.contas.ContaJuridicaForm;
import com.intensivo.java.exception.ResourceNotFoundException;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.TipoCliente;
import com.intensivo.java.model.contas.Conta;
import com.intensivo.java.model.contas.ContaCorrente;
import com.intensivo.java.model.contas.ContaJuridica;
import com.intensivo.java.repository.clientes.ClienteRepository;
import com.intensivo.java.repository.contas.ContaRepository;
import com.intensivo.java.service.contas.ContaService;
import com.intensivo.java.util.TextUtils;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private static final String AGENCIA_PADRAO = "0001";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {
        return contaRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Conta buscar(Long id) {
        return contaRepository.findWithClienteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientesCorrente() {
        return clienteRepository.findAllByTipoClienteOrderByNomeAsc(TipoCliente.PF);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientesJuridicos() {
        return clienteRepository.findAllByTipoClienteOrderByNomeAsc(TipoCliente.PJ);
    }

    @Override
    @Transactional(readOnly = true)
    public ContaCorrente buscarContaCorrente(Long id) {
        Conta conta = buscar(id);
        if (conta instanceof ContaCorrente contaCorrente) {
            return contaCorrente;
        }
        throw new ResourceNotFoundException("Conta corrente nao encontrada.");
    }

    @Override
    @Transactional(readOnly = true)
    public ContaJuridica buscarContaJuridica(Long id) {
        Conta conta = buscar(id);
        if (conta instanceof ContaJuridica contaJuridica) {
            return contaJuridica;
        }
        throw new ResourceNotFoundException("Conta juridica nao encontrada.");
    }

    @Override
    @Transactional
    public Conta criarContaCorrente(ContaCorrenteForm form) {
        Cliente cliente = buscarCliente(form.getClienteId());
        ContaCorrente conta = new ContaCorrente();
        aplicarContaCorrente(conta, cliente, form);
        Conta salva = contaRepository.save(conta);
        log.info("Conta corrente {} criada para cliente {}", salva.getNumero(), cliente.getId());
        return salva;
    }

    @Override
    @Transactional
    public Conta atualizarContaCorrente(Long id, ContaCorrenteForm form) {
        ContaCorrente conta = buscarContaCorrente(id);
        Cliente cliente = buscarCliente(form.getClienteId());
        aplicarContaCorrente(conta, cliente, form);
        log.info("Conta corrente {} atualizada", conta.getNumero());
        return conta;
    }

    @Override
    @Transactional
    public Conta criarContaJuridica(ContaJuridicaForm form) {
        Cliente cliente = buscarCliente(form.getClienteId());
        ContaJuridica conta = new ContaJuridica();
        aplicarContaJuridica(conta, cliente, form);
        Conta salva = contaRepository.save(conta);
        log.info("Conta juridica {} criada para cliente {}", salva.getNumero(), cliente.getId());
        return salva;
    }

    @Override
    @Transactional
    public Conta atualizarContaJuridica(Long id, ContaJuridicaForm form) {
        ContaJuridica conta = buscarContaJuridica(id);
        Cliente cliente = buscarCliente(form.getClienteId());
        aplicarContaJuridica(conta, cliente, form);
        log.info("Conta juridica {} atualizada", conta.getNumero());
        return conta;
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Conta conta = buscar(id);
        contaRepository.delete(conta);
        log.info("Conta {} excluida", conta.getNumero());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarContas() {
        return contaRepository.count();
    }

    private void aplicarContaCorrente(ContaCorrente conta, Cliente cliente, ContaCorrenteForm form) {
        if (conta.getNumero() == null) {
            conta.abrirConta(
                    AGENCIA_PADRAO,
                    gerarNumeroConta(),
                    cliente,
                    form.getSaldoInicial(),
                    form.getStatus(),
                    form.getLimiteChequeEspecial());
            return;
        }
        conta.atualizarDados(cliente, form.getSaldoInicial(), form.getStatus(), form.getLimiteChequeEspecial());
    }

    private void aplicarContaJuridica(ContaJuridica conta, Cliente cliente, ContaJuridicaForm form) {
        String responsavelFinanceiro = TextUtils.textoNormalizado(form.getResponsavelFinanceiro());
        if (conta.getNumero() == null) {
            conta.abrirConta(
                    AGENCIA_PADRAO,
                    gerarNumeroConta(),
                    cliente,
                    form.getSaldoInicial(),
                    form.getStatus(),
                    form.getTaxaPacoteMensal(),
                    responsavelFinanceiro);
            return;
        }
        conta.atualizarDados(cliente, form.getSaldoInicial(), form.getStatus(), form.getTaxaPacoteMensal(),
                responsavelFinanceiro);
    }

    private String gerarNumeroConta() {
        String numero;
        do {
            numero = String.format("%08d", RANDOM.nextInt(100_000_000));
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado para a conta."));
    }
}
