package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.model.TipoCliente;
import com.intensivo.java.repository.ClienteRepository;
import com.intensivo.java.repository.ContaRepository;
import java.util.List;
import java.util.NoSuchElementException;
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

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente nao encontrado."));
    }

    @Override
    @Transactional
    public Cliente criar(Cliente form) {
        String documento = normalizarDocumento(form.getTipoCliente(), form.getDocumento());
        validarDocumentoDuplicado(documento, null, form.getTipoCliente());

        Cliente cliente = new Cliente();
        aplicarCliente(cliente, form, documento);
        Cliente salvo = clienteRepository.save(cliente);
        log.info("Cliente {} criado com documento {}", form.getTipoCliente(), mascararDocumento(documento));
        return salvo;
    }

    @Override
    @Transactional
    public Cliente atualizar(Long id, Cliente form) {
        Cliente cliente = buscar(id);
        String documento = normalizarDocumento(form.getTipoCliente(), form.getDocumento());
        validarDocumentoDuplicado(documento, id, form.getTipoCliente());
        aplicarCliente(cliente, form, documento);
        log.info("Cliente {} atualizado com documento {}", form.getTipoCliente(), mascararDocumento(documento));
        return cliente;
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        if (contaRepository.existsByClienteId(id)) {
            throw new IllegalArgumentException("Nao e possivel excluir um cliente com contas vinculadas.");
        }
        clienteRepository.delete(cliente);
        log.info("Cliente {} excluido", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteRepository.count();
    }

    private void aplicarCliente(Cliente cliente, Cliente form, String documento) {
        Endereco endereco = form.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
        }
        cliente.atualizarCadastro(
                form.getTipoCliente(),
                textoNormalizado(form.getNome()),
                documento,
                emailNormalizado(form.getEmail()),
                somenteDigitos(form.getTelefone()),
                Endereco.builder()
                        .cep(somenteDigitos(endereco.getCep()))
                        .logradouro(textoNormalizado(endereco.getLogradouro()))
                        .numero(textoNormalizado(endereco.getNumero()))
                        .complemento(textoNormalizado(endereco.getComplemento()))
                        .bairro(textoNormalizado(endereco.getBairro()))
                        .cidade(textoNormalizado(endereco.getCidade()))
                        .uf(textoNormalizado(endereco.getUf()))
                        .build());
    }

    private void validarDocumentoDuplicado(String documento, Long id, TipoCliente tipoCliente) {
        boolean duplicado = id == null
                ? clienteRepository.existsByDocumento(documento)
                : clienteRepository.existsByDocumentoAndIdNot(documento, id);
        if (duplicado) {
            throw new IllegalArgumentException("Ja existe um cliente com este " + tipoCliente.getDocumentoLabel() + ".");
        }
    }

    private String normalizarDocumento(TipoCliente tipoCliente, String documento) {
        String normalizado = somenteDigitos(documento);
        if (tipoCliente == null) {
            throw new IllegalArgumentException("Tipo de cliente invalido.");
        }
        if (normalizado.length() != tipoCliente.getTamanhoDocumento()) {
            throw new IllegalArgumentException(
                    tipoCliente.getDocumentoLabel() + " invalido. Informe " + tipoCliente.getTamanhoDocumento() + " digitos.");
        }
        return normalizado;
    }

    private String somenteDigitos(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("\\D", "");
    }

    private String textoNormalizado(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s{2,}", " ");
    }

    private String emailNormalizado(String valor) {
        return textoNormalizado(valor).toLowerCase();
    }

    private String mascararDocumento(String valor) {
        String digits = somenteDigitos(valor);
        if (digits.length() <= 4) {
            return "****";
        }
        return "*".repeat(Math.max(0, digits.length() - 4)) + digits.substring(digits.length() - 4);
    }
}
