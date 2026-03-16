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
        if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existe pessoa cadastrada com esse cpf.");
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        if (clienteRepository.existsByDocumentoAndIdNot(clienteAtualizado.getDocumento(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existe pessoa cadastrada com esse cpf.");
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
}
