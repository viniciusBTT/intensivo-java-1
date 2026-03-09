package com.intensivo.java.service.clientes;

import com.intensivo.java.dto.form.clientes.ClienteForm;
import com.intensivo.java.model.clientes.Cliente;
import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    Cliente buscar(Long id);

    Cliente criar(ClienteForm form);

    Cliente atualizar(Long id, ClienteForm form);

    void excluir(Long id);

    long contarClientes();
}
