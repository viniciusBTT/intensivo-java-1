package com.intensivo.java.exception.clientes;

import com.intensivo.java.exception.BusinessException;

public class EntityInUseException extends BusinessException {

    public EntityInUseException(String message) {
        super(message);
    }
}
