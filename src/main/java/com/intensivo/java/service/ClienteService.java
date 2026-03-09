package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.dto.form.ClienteForm;
import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    Cliente buscar(Long id);

    Cliente criar(ClienteForm form);

    Cliente atualizar(Long id, ClienteForm form);

    void excluir(Long id);

    long contarClientes();
}
