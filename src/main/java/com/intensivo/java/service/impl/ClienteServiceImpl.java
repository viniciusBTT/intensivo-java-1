package com.intensivo.java.service.impl;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.dto.form.ClientePessoaFisicaForm;
import com.intensivo.java.dto.form.ClientePessoaJuridicaForm;
import com.intensivo.java.exception.DuplicateDocumentException;
import com.intensivo.java.exception.EntityInUseException;
import com.intensivo.java.exception.ResourceNotFoundException;
import com.intensivo.java.repository.ClientePessoaFisicaRepository;
import com.intensivo.java.repository.ClientePessoaJuridicaRepository;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.service.CepLookupService;
import com.intensivo.java.service.ClienteService;
import com.intensivo.java.service.support.MaskingUtils;
import com.intensivo.java.service.support.TextUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClientePessoaFisicaRepository clientePessoaFisicaRepository;
    private final ClientePessoaJuridicaRepository clientePessoaJuridicaRepository;
    private final ContaRepository contaRepository;
    private final CepLookupService cepLookupService;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientePessoaFisica> listarPessoasFisicas() {
        return clientePessoaFisicaRepository.findAllByOrderByNomeCompletoAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientePessoaJuridica> listarPessoasJuridicas() {
        return clientePessoaJuridicaRepository.findAllByOrderByNomeFantasiaAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePessoaFisica buscarPessoaFisica(Long id) {
        return clientePessoaFisicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente PF nao encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePessoaJuridica buscarPessoaJuridica(Long id) {
        return clientePessoaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente PJ nao encontrado."));
    }

    @Override
    @Transactional
    public Cliente criarPessoaFisica(ClientePessoaFisicaForm form) {
        String cpf = normalizarCpf(form.getCpf());
        validarCpfDuplicado(cpf, null);

        ClientePessoaFisica cliente = new ClientePessoaFisica();
        aplicarPessoaFisica(cliente, form, cpf);
        Cliente salvo = clientePessoaFisicaRepository.save(cliente);
        log.info("Cliente PF criado com documento {}", MaskingUtils.maskDocument(cpf));
        return salvo;
    }

    @Override
    @Transactional
    public Cliente atualizarPessoaFisica(Long id, ClientePessoaFisicaForm form) {
        ClientePessoaFisica cliente = buscarPessoaFisica(id);
        String cpf = normalizarCpf(form.getCpf());
        validarCpfDuplicado(cpf, id);
        aplicarPessoaFisica(cliente, form, cpf);
        log.info("Cliente PF atualizado com documento {}", MaskingUtils.maskDocument(cpf));
        return cliente;
    }

    @Override
    @Transactional
    public Cliente criarPessoaJuridica(ClientePessoaJuridicaForm form) {
        String cnpj = normalizarCnpj(form.getCnpj());
        validarCnpjDuplicado(cnpj, null);

        ClientePessoaJuridica cliente = new ClientePessoaJuridica();
        aplicarPessoaJuridica(cliente, form, cnpj);
        Cliente salvo = clientePessoaJuridicaRepository.save(cliente);
        log.info("Cliente PJ criado com documento {}", MaskingUtils.maskDocument(cnpj));
        return salvo;
    }

    @Override
    @Transactional
    public Cliente atualizarPessoaJuridica(Long id, ClientePessoaJuridicaForm form) {
        ClientePessoaJuridica cliente = buscarPessoaJuridica(id);
        String cnpj = normalizarCnpj(form.getCnpj());
        validarCnpjDuplicado(cnpj, id);
        aplicarPessoaJuridica(cliente, form, cnpj);
        log.info("Cliente PJ atualizado com documento {}", MaskingUtils.maskDocument(cnpj));
        return cliente;
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado."));
        if (contaRepository.existsByClienteId(id)) {
            throw new EntityInUseException("Nao e possivel excluir um cliente com contas vinculadas.");
        }
        clienteRepository.delete(cliente);
        log.info("Cliente {} excluido", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteRepository.count();
    }

    private void aplicarPessoaFisica(ClientePessoaFisica cliente, ClientePessoaFisicaForm form, String cpf) {
        cliente.setNomeCompleto(TextUtils.textoNormalizado(form.getNomeCompleto()));
        cliente.setCpf(cpf);
        aplicarCamposComuns(cliente, form);
    }

    private void aplicarPessoaJuridica(ClientePessoaJuridica cliente, ClientePessoaJuridicaForm form, String cnpj) {
        cliente.setRazaoSocial(TextUtils.textoNormalizado(form.getRazaoSocial()));
        cliente.setNomeFantasia(TextUtils.textoNormalizado(form.getNomeFantasia()));
        cliente.setCnpj(cnpj);
        aplicarCamposComuns(cliente, form);
    }

    private void aplicarCamposComuns(Cliente cliente, com.intensivo.java.dto.form.ClienteForm form) {
        cliente.setEmail(TextUtils.emailNormalizado(form.getEmail()));
        cliente.setTelefone(TextUtils.somenteDigitos(form.getTelefone()));
        cliente.setEndereco(cepLookupService.resolverEndereco(form.getCep(), form.getNumero(), form.getComplemento()));
    }

    private void validarCpfDuplicado(String cpf, Long id) {
        boolean duplicado = id == null
                ? clientePessoaFisicaRepository.existsByCpf(cpf)
                : clientePessoaFisicaRepository.existsByCpfAndIdNot(cpf, id);
        if (duplicado) {
            throw new DuplicateDocumentException("Ja existe um cliente PF com este CPF.");
        }
    }

    private void validarCnpjDuplicado(String cnpj, Long id) {
        boolean duplicado = id == null
                ? clientePessoaJuridicaRepository.existsByCnpj(cnpj)
                : clientePessoaJuridicaRepository.existsByCnpjAndIdNot(cnpj, id);
        if (duplicado) {
            throw new DuplicateDocumentException("Ja existe um cliente PJ com este CNPJ.");
        }
    }

    private String normalizarCpf(String cpf) {
        String normalizado = TextUtils.somenteDigitos(cpf);
        if (normalizado.length() != 11) {
            throw new DuplicateDocumentException("CPF invalido. Informe 11 digitos.");
        }
        return normalizado;
    }

    private String normalizarCnpj(String cnpj) {
        String normalizado = TextUtils.somenteDigitos(cnpj);
        if (normalizado.length() != 14) {
            throw new DuplicateDocumentException("CNPJ invalido. Informe 14 digitos.");
        }
        return normalizado;
    }
}
