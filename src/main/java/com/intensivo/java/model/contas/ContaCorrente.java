package com.intensivo.java.model.contas;

import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "contas_corrente")
public class ContaCorrente extends Conta {

    private static final BigDecimal TARIFA_PADRAO = new BigDecimal("12.50");

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limiteChequeEspecial;

    public void abrirConta(String agencia, String numero, Cliente cliente, BigDecimal saldoInicial, ContaStatus status,
            BigDecimal limiteChequeEspecial) {
        super.abrirConta(agencia, numero, cliente, saldoInicial, status);
        this.limiteChequeEspecial = valorMonetario(limiteChequeEspecial);
    }

    public void atualizarDados(Cliente cliente, BigDecimal saldoInicial, ContaStatus status, BigDecimal limiteChequeEspecial) {
        atualizarDadosCadastrais(cliente, saldoInicial, status);
        this.limiteChequeEspecial = valorMonetario(limiteChequeEspecial);
    }

    @Override
    public BigDecimal calcularTarifaMensal() {
        return TARIFA_PADRAO;
    }

    @Override
    public boolean aceitaTipoCliente(TipoCliente tipoCliente) {
        return tipoCliente == TipoCliente.PF;
    }

    @Override
    public String getTipoConta() {
        return "Conta Corrente";
    }
}
