package com.intensivo.java.dto.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaJuridicaForm extends ContaForm {

    @NotNull(message = "Informe a tarifa mensal.")
    @DecimalMin(value = "0.00", message = "A tarifa nao pode ser negativa.")
    private BigDecimal taxaPacoteMensal;

    @NotBlank(message = "Informe o responsavel financeiro.")
    private String responsavelFinanceiro;
}
