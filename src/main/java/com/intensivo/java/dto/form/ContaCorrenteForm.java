package com.intensivo.java.dto.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaCorrenteForm extends ContaForm {

    @NotNull(message = "Informe o limite do cheque especial.")
    @DecimalMin(value = "0.00", message = "O limite nao pode ser negativo.")
    private BigDecimal limiteChequeEspecial;
}
