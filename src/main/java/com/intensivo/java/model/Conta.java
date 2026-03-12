package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContaTipo tipo;

    @Column(nullable = false, length = 4)
    private String agencia;

    @Column(nullable = false, unique = true, length = 12)
    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal tarifaMensal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContaStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limiteChequeEspecial;

    @Column(nullable = false)
    private String responsavelFinanceiro;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    public void abrirConta(String agencia, String numero, Cliente cliente, BigDecimal saldoInicial, ContaStatus status) {
        this.agencia = Objects.requireNonNull(agencia, "Agencia obrigatoria.");
        this.numero = Objects.requireNonNull(numero, "Numero obrigatorio.");
        atualizarDadosCadastrais(cliente, saldoInicial, status);
    }

    public void atualizarDadosCadastrais(Cliente cliente, BigDecimal saldoInicial, ContaStatus status) {
        vincularCliente(cliente);
        this.saldoInicial = valorMonetario(saldoInicial);
        this.status = Objects.requireNonNull(status, "Status obrigatorio.");
        aplicarRegrasTipo();
    }

    public void vincularCliente(Cliente cliente) {
        Cliente clienteObrigatorio = Objects.requireNonNull(cliente, "Cliente obrigatorio.");
        if (!aceitaTipoCliente(clienteObrigatorio.getTipoCliente(), tipo)) {
            throw new IllegalArgumentException("O tipo de cliente nao e compativel com a conta selecionada.");
        }
        this.cliente = clienteObrigatorio;
    }

    protected BigDecimal valorMonetario(BigDecimal value) {
        BigDecimal valor = Objects.requireNonNullElse(value, BigDecimal.ZERO);
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTarifaMensal() {
        return tarifaMensal == null ? BigDecimal.ZERO : tarifaMensal;
    }

    public boolean aceitaTipoCliente(TipoCliente tipoCliente, ContaTipo contaTipo) {
        if (contaTipo == ContaTipo.CORRENTE) {
            return tipoCliente == TipoCliente.PF;
        }
        return tipoCliente == TipoCliente.PJ;
    }

    public String getTipoConta() {
        return tipo == ContaTipo.CORRENTE ? "Conta Corrente" : "Conta Juridica";
    }

    public void aplicarRegrasTipo() {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da conta obrigatorio.");
        }

        if (tipo == ContaTipo.CORRENTE) {
            limiteChequeEspecial = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            tarifaMensal = new BigDecimal("12.50");
            responsavelFinanceiro = "";
            return;
        }

        limiteChequeEspecial = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        tarifaMensal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        responsavelFinanceiro = "";
    }

    @PrePersist
    void prePersist() {
        OffsetDateTime agora = OffsetDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = OffsetDateTime.now();
    }
}
