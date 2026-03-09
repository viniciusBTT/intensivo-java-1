package com.intensivo.java.service.impl;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.Conta;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.model.TipoCliente;
import com.intensivo.java.dto.form.ContaCorrenteForm;
import com.intensivo.java.dto.form.ContaJuridicaForm;
import com.intensivo.java.exception.ContaClienteIncompativelException;
import com.intensivo.java.exception.ResourceNotFoundException;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.service.ContaService;
import com.intensivo.java.util.TextUtils;
import java.math.BigDecimal;
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
        conta.setNumero(gerarNumeroConta());
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
        conta.setNumero(gerarNumeroConta());
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
        validarTipoCliente(conta, cliente);
        conta.setAgencia(AGENCIA_PADRAO);
        conta.setCliente(cliente);
        conta.setSaldoInicial(valorMonetario(form.getSaldoInicial()));
        conta.setStatus(form.getStatus());
        conta.setLimiteChequeEspecial(valorMonetario(form.getLimiteChequeEspecial()));
    }

    private void aplicarContaJuridica(ContaJuridica conta, Cliente cliente, ContaJuridicaForm form) {
        validarTipoCliente(conta, cliente);
        conta.setAgencia(AGENCIA_PADRAO);
        conta.setCliente(cliente);
        conta.setSaldoInicial(valorMonetario(form.getSaldoInicial()));
        conta.setStatus(form.getStatus());
        conta.setTaxaPacoteMensal(valorMonetario(form.getTaxaPacoteMensal()));
        conta.setResponsavelFinanceiro(TextUtils.textoNormalizado(form.getResponsavelFinanceiro()));
    }

    private void validarTipoCliente(Conta conta, Cliente cliente) {
        if (!conta.aceitaTipoCliente(cliente.getTipoCliente())) {
            throw new ContaClienteIncompativelException("O tipo de cliente nao e compativel com a conta selecionada.");
        }
    }

    private BigDecimal valorMonetario(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, java.math.RoundingMode.HALF_UP);
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
