package com.intensivo.java.exception.contas;

import com.intensivo.java.exception.BusinessException;

public class ContaClienteIncompativelException extends BusinessException {

    public ContaClienteIncompativelException(String message) {
        super(message);
    }
}
