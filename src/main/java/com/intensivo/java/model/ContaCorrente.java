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
@Table(name = "contas_corrente")
public class ContaCorrente extends Conta {

    private static final BigDecimal TARIFA_PADRAO = new BigDecimal("12.50");

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limiteChequeEspecial;

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
