package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.repository.ClienteRepository;
import java.util.List;
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
        cliente.normalizarCampos();
        validarDocumentoDisponivel(cliente.getDocumento(), null);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        clienteAtualizado.normalizarCampos();
        validarDocumentoDisponivel(clienteAtualizado.getDocumento(), id);
        clienteExistente.atualizarCom(clienteAtualizado);

        return clienteRepository.save(clienteExistente);
    }

    private void validarDocumentoDisponivel(String documento, Long id) {
        boolean documentoEmUso = id == null
                ? clienteRepository.existsByDocumento(documento)
                : clienteRepository.existsByDocumentoAndIdNot(documento, id);

        if (documentoEmUso) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existe pessoa cadastrada com esse cpf.");
        }
    }
}
