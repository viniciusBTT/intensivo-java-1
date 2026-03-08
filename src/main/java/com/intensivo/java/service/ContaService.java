package com.intensivo.java.service;

import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.model.Conta;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.dto.form.ContaCorrenteForm;
import com.intensivo.java.dto.form.ContaJuridicaForm;
import java.util.List;

public interface ContaService {

    List<Conta> listarTodas();

    Conta buscar(Long id);

    List<ClientePessoaFisica> listarClientesCorrente();

    List<ClientePessoaJuridica> listarClientesJuridicos();

    ContaCorrente buscarContaCorrente(Long id);

    ContaJuridica buscarContaJuridica(Long id);

    Conta criarContaCorrente(ContaCorrenteForm form);

    Conta atualizarContaCorrente(Long id, ContaCorrenteForm form);

    Conta criarContaJuridica(ContaJuridicaForm form);

    Conta atualizarContaJuridica(Long id, ContaJuridicaForm form);

    void excluir(Long id);

    long contarContas();
}
