package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.Conta;
import java.util.List;

public interface ContaService {

    List<Conta> listarTodas();

    Conta buscar(Long id);

    List<Cliente> listarClientes();

    Conta criar(Conta conta);

    Conta atualizar(Long id, Conta conta);

    void excluir(Long id);

    long contarContas();
}
