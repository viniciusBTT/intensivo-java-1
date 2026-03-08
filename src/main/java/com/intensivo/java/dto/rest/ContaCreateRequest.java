package com.intensivo.java.dto.rest;

import com.intensivo.java.model.ContaStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaCreateRequest {

    @NotNull(message = "Informe o tipo da conta.")
    private ContaRestType tipo;

    @NotNull(message = "Selecione um cliente.")
    private Long clienteId;

    @NotNull(message = "Informe o saldo inicial.")
    @DecimalMin(value = "0.00", message = "O saldo inicial nao pode ser negativo.")
    private BigDecimal saldoInicial;

    private ContaStatus status = ContaStatus.ATIVA;

    private BigDecimal limiteChequeEspecial;

    private BigDecimal taxaPacoteMensal;

    private String responsavelFinanceiro;
}
