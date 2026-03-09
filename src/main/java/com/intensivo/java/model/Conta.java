package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Conta extends BaseEntity {

    @Column(nullable = false, length = 4)
    private String agencia;

    @Column(nullable = false, unique = true, length = 12)
    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContaStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public abstract BigDecimal calcularTarifaMensal();

    public abstract boolean aceitaTipoCliente(TipoCliente tipoCliente);

    public abstract String getTipoConta();
}
