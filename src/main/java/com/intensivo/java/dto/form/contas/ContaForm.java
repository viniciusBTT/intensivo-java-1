package com.intensivo.java.dto.form.contas;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ContaForm {

    private String clienteId;
    private BigDecimal saldoInicial;
    private String status = "ATIVA";
}
