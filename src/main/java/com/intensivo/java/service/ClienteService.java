package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    Cliente buscar(Long id);

    Cliente criar(Cliente cliente);

    Cliente atualizar(Long id, Cliente cliente);

    void excluir(Long id);

    long contarClientes();
}
