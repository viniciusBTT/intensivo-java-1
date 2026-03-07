package com.intensivo.java.service;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.dto.form.ClientePessoaFisicaForm;
import com.intensivo.java.dto.form.ClientePessoaJuridicaForm;
import java.util.List;

public interface ClienteService {

    List<Cliente> listarTodos();

    List<ClientePessoaFisica> listarPessoasFisicas();

    List<ClientePessoaJuridica> listarPessoasJuridicas();

    ClientePessoaFisica buscarPessoaFisica(Long id);

    ClientePessoaJuridica buscarPessoaJuridica(Long id);

    Cliente criarPessoaFisica(ClientePessoaFisicaForm form);

    Cliente atualizarPessoaFisica(Long id, ClientePessoaFisicaForm form);

    Cliente criarPessoaJuridica(ClientePessoaJuridicaForm form);

    Cliente atualizarPessoaJuridica(Long id, ClientePessoaJuridicaForm form);

    void excluir(Long id);

    long contarClientes();
}
