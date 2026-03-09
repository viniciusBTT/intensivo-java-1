package com.intensivo.java.service.contas;

import com.intensivo.java.dto.form.contas.ContaCorrenteForm;
import com.intensivo.java.dto.form.contas.ContaJuridicaForm;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.contas.Conta;
import com.intensivo.java.model.contas.ContaCorrente;
import com.intensivo.java.model.contas.ContaJuridica;
import java.util.List;

public interface ContaService {

    List<Conta> listarTodas();

    Conta buscar(Long id);

    List<Cliente> listarClientesCorrente();

    List<Cliente> listarClientesJuridicos();

    ContaCorrente buscarContaCorrente(Long id);

    ContaJuridica buscarContaJuridica(Long id);

    Conta criarContaCorrente(ContaCorrenteForm form);

    Conta atualizarContaCorrente(Long id, ContaCorrenteForm form);

    Conta criarContaJuridica(ContaJuridicaForm form);

    Conta atualizarContaJuridica(Long id, ContaJuridicaForm form);

    void excluir(Long id);

    long contarContas();
}
