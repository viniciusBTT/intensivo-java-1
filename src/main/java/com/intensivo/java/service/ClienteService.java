package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.repository.ClienteRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado."));
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        cliente.setId(null);
        normalizarCampos(cliente);
        validarDocumentoDisponivel(cliente.getDocumento());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        normalizarCampos(clienteAtualizado);

        if (!Objects.equals(clienteAtualizado.getDocumento(), clienteExistente.getDocumento())) {
            validarDocumentoDisponivel(clienteAtualizado.getDocumento());
        }

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setDocumento(clienteAtualizado.getDocumento());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setCep(clienteAtualizado.getCep());
        clienteExistente.setLogradouro(clienteAtualizado.getLogradouro());
        clienteExistente.setNumero(clienteAtualizado.getNumero());
        clienteExistente.setComplemento(clienteAtualizado.getComplemento());
        clienteExistente.setBairro(clienteAtualizado.getBairro());
        clienteExistente.setCidade(clienteAtualizado.getCidade());
        clienteExistente.setUf(clienteAtualizado.getUf());

        return clienteRepository.save(clienteExistente);
    }

    private void validarDocumentoDisponivel(String documento) {
        boolean documentoEmUso = clienteRepository.existsByDocumento(documento);

        if (documentoEmUso) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existe pessoa cadastrada com esse cpf.");
        }
    }

    private void normalizarCampos(Cliente cliente) {
        cliente.setNome(normalizar(cliente.getNome()));
        cliente.setDocumento(normalizar(cliente.getDocumento()));

        String email = normalizar(cliente.getEmail());
        cliente.setEmail(email == null ? null : email.toLowerCase(Locale.ROOT));

        String cep = normalizar(cliente.getCep());
        cliente.setCep(cep == null ? null : cep.replaceAll("\\D", ""));

        cliente.setTelefone(normalizar(cliente.getTelefone()));
        cliente.setLogradouro(normalizar(cliente.getLogradouro()));
        cliente.setNumero(normalizar(cliente.getNumero()));
        cliente.setComplemento(normalizar(cliente.getComplemento()));
        cliente.setBairro(normalizar(cliente.getBairro()));
        cliente.setCidade(normalizar(cliente.getCidade()));

        String uf = normalizar(cliente.getUf());
        cliente.setUf(uf == null ? null : uf.toUpperCase(Locale.ROOT));
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }

        String valorNormalizado = valor.trim();
        return valorNormalizado.isEmpty() ? null : valorNormalizado;
    }
}
