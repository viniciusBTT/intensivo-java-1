package com.intensivo.java.exception.clientes;

import com.intensivo.java.exception.BusinessException;

public class DuplicateDocumentException extends BusinessException {

    public DuplicateDocumentException(String message) {
        super(message);
    }
}
