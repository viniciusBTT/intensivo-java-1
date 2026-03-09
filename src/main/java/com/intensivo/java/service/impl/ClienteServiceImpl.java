package com.intensivo.java.service.impl;

import com.intensivo.java.dto.form.ClienteForm;
import com.intensivo.java.exception.DuplicateDocumentException;
import com.intensivo.java.exception.EntityInUseException;
import com.intensivo.java.exception.ResourceNotFoundException;
import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.TipoCliente;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import com.intensivo.java.service.CepLookupService;
import com.intensivo.java.service.ClienteService;
import com.intensivo.java.util.MaskingUtils;
import com.intensivo.java.util.TextUtils;
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
    private final ContaRepository contaRepository;
    private final CepLookupService cepLookupService;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado."));
    }

    @Override
    @Transactional
    public Cliente criar(ClienteForm form) {
        String documento = normalizarDocumento(form.getTipoCliente(), form.getDocumento());
        validarDocumentoDuplicado(documento, null, form.getTipoCliente());

        Cliente cliente = new Cliente();
        aplicarCliente(cliente, form, documento);
        Cliente salvo = clienteRepository.save(cliente);
        log.info("Cliente {} criado com documento {}", form.getTipoCliente(), MaskingUtils.maskDocument(documento));
        return salvo;
    }

    @Override
    @Transactional
    public Cliente atualizar(Long id, ClienteForm form) {
        Cliente cliente = buscar(id);
        String documento = normalizarDocumento(form.getTipoCliente(), form.getDocumento());
        validarDocumentoDuplicado(documento, id, form.getTipoCliente());
        aplicarCliente(cliente, form, documento);
        log.info("Cliente {} atualizado com documento {}", form.getTipoCliente(), MaskingUtils.maskDocument(documento));
        return cliente;
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
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

    private void aplicarCliente(Cliente cliente, ClienteForm form, String documento) {
        cliente.setTipoCliente(form.getTipoCliente());
        cliente.setNome(TextUtils.textoNormalizado(form.getNome()));
        cliente.setDocumento(documento);
        cliente.setEmail(TextUtils.emailNormalizado(form.getEmail()));
        cliente.setTelefone(TextUtils.somenteDigitos(form.getTelefone()));
        cliente.setEndereco(cepLookupService.resolverEndereco(form.getCep(), form.getNumero(), form.getComplemento()));
    }

    private void validarDocumentoDuplicado(String documento, Long id, TipoCliente tipoCliente) {
        boolean duplicado = id == null
                ? clienteRepository.existsByDocumento(documento)
                : clienteRepository.existsByDocumentoAndIdNot(documento, id);
        if (duplicado) {
            throw new DuplicateDocumentException("Ja existe um cliente com este " + tipoCliente.getDocumentoLabel() + ".");
        }
    }

    private String normalizarDocumento(TipoCliente tipoCliente, String documento) {
        String normalizado = TextUtils.somenteDigitos(documento);
        if (tipoCliente == null) {
            throw new DuplicateDocumentException("Tipo de cliente invalido.");
        }
        if (normalizado.length() != tipoCliente.getTamanhoDocumento()) {
            throw new DuplicateDocumentException(
                    tipoCliente.getDocumentoLabel() + " invalido. Informe " + tipoCliente.getTamanhoDocumento() + " digitos.");
        }
        return normalizado;
    }
}
