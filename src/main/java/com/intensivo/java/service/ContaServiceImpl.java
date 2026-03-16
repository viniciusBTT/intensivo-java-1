package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.Conta;
import com.intensivo.java.model.ContaTipo;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;
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
                .orElseThrow(() -> new NoSuchElementException("Conta nao encontrada."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional
    public Conta criar(Conta form) {
        Conta conta = salvar(new Conta(), form);
        log.info("Conta {} criada para cliente {}", conta.getNumero(), conta.getCliente().getId());
        return conta;
    }

    @Override
    @Transactional
    public Conta atualizar(Long id, Conta form) {
        Conta conta = salvar(buscar(id), form);
        log.info("Conta {} atualizada", conta.getNumero());
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

    private Conta salvar(Conta conta, Conta form) {
        Cliente cliente = buscarCliente(form.getCliente().getId());
        aplicarConta(conta, cliente, form);
        return contaRepository.save(conta);
    }

    private void aplicarConta(Conta conta, Cliente cliente, Conta form) {
        conta.setTipo(form.getTipo() == null ? ContaTipo.CORRENTE : form.getTipo());
        if (conta.getNumero() == null) {
            conta.abrirConta(
                    AGENCIA_PADRAO,
                    gerarNumeroConta(),
                    cliente,
                    form.getSaldoInicial(),
                    form.getStatus());
            return;
        }
        conta.atualizarDadosCadastrais(cliente, form.getSaldoInicial(), form.getStatus());
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
                .orElseThrow(() -> new NoSuchElementException("Cliente nao encontrado para a conta."));
    }
}
