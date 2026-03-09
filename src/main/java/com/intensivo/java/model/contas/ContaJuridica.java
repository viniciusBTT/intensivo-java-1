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
@Table(name = "contas_juridicas")
public class ContaJuridica extends Conta {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal taxaPacoteMensal;

    @Column(nullable = false)
    private String responsavelFinanceiro;

    public void abrirConta(String agencia, String numero, Cliente cliente, BigDecimal saldoInicial, ContaStatus status,
            BigDecimal taxaPacoteMensal, String responsavelFinanceiro) {
        super.abrirConta(agencia, numero, cliente, saldoInicial, status);
        this.taxaPacoteMensal = valorMonetario(taxaPacoteMensal);
        this.responsavelFinanceiro = responsavelFinanceiro;
    }

    public void atualizarDados(Cliente cliente, BigDecimal saldoInicial, ContaStatus status, BigDecimal taxaPacoteMensal,
            String responsavelFinanceiro) {
        atualizarDadosCadastrais(cliente, saldoInicial, status);
        this.taxaPacoteMensal = valorMonetario(taxaPacoteMensal);
        this.responsavelFinanceiro = responsavelFinanceiro;
    }

    @Override
    public BigDecimal calcularTarifaMensal() {
        return taxaPacoteMensal;
    }

    @Override
    public boolean aceitaTipoCliente(TipoCliente tipoCliente) {
        return tipoCliente == TipoCliente.PJ;
    }

    @Override
    public String getTipoConta() {
        return "Conta Juridica";
    }
}
