package com.intensivo.java.model.contas;

import com.intensivo.java.exception.contas.ContaClienteIncompativelException;
import com.intensivo.java.model.BaseEntity;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.TipoCliente;
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
import java.math.RoundingMode;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    public void abrirConta(String agencia, String numero, Cliente cliente, BigDecimal saldoInicial, ContaStatus status) {
        this.agencia = Objects.requireNonNull(agencia, "Agencia obrigatoria.");
        this.numero = Objects.requireNonNull(numero, "Numero obrigatorio.");
        atualizarDadosCadastrais(cliente, saldoInicial, status);
    }

    protected void atualizarDadosCadastrais(Cliente cliente, BigDecimal saldoInicial, ContaStatus status) {
        vincularCliente(cliente);
        this.saldoInicial = valorMonetario(saldoInicial);
        this.status = Objects.requireNonNull(status, "Status obrigatorio.");
    }

    public void vincularCliente(Cliente cliente) {
        Cliente clienteObrigatorio = Objects.requireNonNull(cliente, "Cliente obrigatorio.");
        if (!aceitaTipoCliente(clienteObrigatorio.getTipoCliente())) {
            throw new ContaClienteIncompativelException("O tipo de cliente nao e compativel com a conta selecionada.");
        }
        this.cliente = clienteObrigatorio;
    }

    protected BigDecimal valorMonetario(BigDecimal value) {
        BigDecimal valor = Objects.requireNonNullElse(value, BigDecimal.ZERO);
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public abstract BigDecimal calcularTarifaMensal();

    public abstract boolean aceitaTipoCliente(TipoCliente tipoCliente);

    public abstract String getTipoConta();
}
