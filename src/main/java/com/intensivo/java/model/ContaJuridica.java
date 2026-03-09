package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contas_juridicas")
public class ContaJuridica extends Conta {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal taxaPacoteMensal;

    @Column(nullable = false)
    private String responsavelFinanceiro;

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
