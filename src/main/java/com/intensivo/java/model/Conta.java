package com.intensivo.java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Locale;
@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o numero da conta.")
    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @NotBlank(message = "Informe a agencia.")
    @Column(nullable = false, length = 10)
    private String agencia;

    @NotBlank(message = "Informe o tipo da conta.")
    @Column(nullable = false, length = 20)
    private String tipo;

    @NotNull(message = "Informe o saldo.")
    @DecimalMin(value = "0.00", message = "O saldo nao pode ser negativo.")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean ativa = true;

    public Conta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    @PrePersist
    @PreUpdate
    public void normalizarCampos() {
        numero = normalizar(numero);
        agencia = normalizar(agencia);
        tipo = normalizar(tipo).toUpperCase(Locale.ROOT);
    }

    private String normalizar(String valor) {
        return valor.trim();
    }
}
