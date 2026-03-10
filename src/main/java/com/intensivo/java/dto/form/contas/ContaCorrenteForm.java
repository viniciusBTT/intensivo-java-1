package com.intensivo.java.dto.form.contas;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaCorrenteForm extends ContaForm {

    private BigDecimal limiteChequeEspecial;
}
