package com.intensivo.java.dto.form.contas;

import com.intensivo.java.model.contas.ContaStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ContaForm {

    private Long id;

    @NotNull(message = "Selecione um cliente.")
    private Long clienteId;

    @NotNull(message = "Informe o saldo inicial.")
    @DecimalMin(value = "0.00", message = "O saldo inicial nao pode ser negativo.")
    private BigDecimal saldoInicial;

    @NotNull(message = "Selecione um status.")
    private ContaStatus status = ContaStatus.ATIVA;
}
