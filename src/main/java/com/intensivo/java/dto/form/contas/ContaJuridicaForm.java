package com.intensivo.java.dto.form.contas;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaJuridicaForm extends ContaForm {

    private BigDecimal taxaPacoteMensal;
    private String responsavelFinanceiro;
}
