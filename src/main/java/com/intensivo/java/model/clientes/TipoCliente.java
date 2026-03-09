package com.intensivo.java.model.clientes;

public enum TipoCliente {
    PF("Pessoa Fisica", "CPF", 11),
    PJ("Pessoa Juridica", "CNPJ", 14);

    private final String descricao;
    private final String documentoLabel;
    private final int tamanhoDocumento;

    TipoCliente(String descricao, String documentoLabel, int tamanhoDocumento) {
        this.descricao = descricao;
        this.documentoLabel = documentoLabel;
        this.tamanhoDocumento = tamanhoDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDocumentoLabel() {
        return documentoLabel;
    }

    public int getTamanhoDocumento() {
        return tamanhoDocumento;
    }
}
