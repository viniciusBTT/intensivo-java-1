package com.intensivo.java.dto.rest;

import com.intensivo.java.model.ContaStatus;
import java.math.BigDecimal;

public record ContaResponse(
        Long id,
        ContaRestType tipo,
        String numero,
        String agencia,
        ContaStatus status,
        BigDecimal saldoInicial,
        BigDecimal tarifaMensal,
        Long clienteId,
        String clienteNome,
        BigDecimal limiteChequeEspecial,
        BigDecimal taxaPacoteMensal,
        String responsavelFinanceiro) {
}
