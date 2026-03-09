package com.intensivo.java.controller.support;

import com.intensivo.java.dto.form.clientes.ClienteForm;
import com.intensivo.java.dto.form.contas.ContaCorrenteForm;
import com.intensivo.java.dto.form.contas.ContaForm;
import com.intensivo.java.dto.form.contas.ContaJuridicaForm;
import com.intensivo.java.dto.rest.clientes.ClienteCreateRequest;
import com.intensivo.java.dto.rest.clientes.ClienteResponse;
import com.intensivo.java.dto.rest.clientes.EnderecoResponse;
import com.intensivo.java.dto.rest.contas.ContaCreateRequest;
import com.intensivo.java.dto.rest.contas.ContaResponse;
import com.intensivo.java.dto.rest.contas.ContaRestType;
import com.intensivo.java.exception.RestValidationException;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.Endereco;
import com.intensivo.java.model.contas.Conta;
import com.intensivo.java.model.contas.ContaCorrente;
import com.intensivo.java.model.contas.ContaJuridica;
import com.intensivo.java.model.contas.ContaStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestPayloadMapper {

    private final Validator validator;

    public ClienteForm toClienteForm(ClienteCreateRequest request) {
        ClienteForm form = new ClienteForm();
        preencherClienteComum(form, request);
        validate(form);
        return form;
    }

    public ContaCorrenteForm toContaCorrenteForm(ContaCreateRequest request) {
        ContaCorrenteForm form = new ContaCorrenteForm();
        preencherContaComum(form, request);
        form.setLimiteChequeEspecial(request.getLimiteChequeEspecial());
        validate(form);
        return form;
    }

    public ContaJuridicaForm toContaJuridicaForm(ContaCreateRequest request) {
        ContaJuridicaForm form = new ContaJuridicaForm();
        preencherContaComum(form, request);
        form.setTaxaPacoteMensal(request.getTaxaPacoteMensal());
        form.setResponsavelFinanceiro(request.getResponsavelFinanceiro());
        validate(form);
        return form;
    }

    public ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoCliente(),
                cliente.getNome(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cliente.getTelefone(),
                toEnderecoResponse(cliente.getEndereco()));
    }

    public ContaResponse toContaResponse(Conta conta) {
        if (conta instanceof ContaCorrente contaCorrente) {
            return new ContaResponse(
                    contaCorrente.getId(),
                    ContaRestType.CORRENTE,
                    contaCorrente.getNumero(),
                    contaCorrente.getAgencia(),
                    contaCorrente.getStatus(),
                    contaCorrente.getSaldoInicial(),
                    contaCorrente.calcularTarifaMensal(),
                    contaCorrente.getCliente().getId(),
                    contaCorrente.getCliente().getNome(),
                    contaCorrente.getLimiteChequeEspecial(),
                    null,
                    null);
        }

        if (conta instanceof ContaJuridica contaJuridica) {
            return new ContaResponse(
                    contaJuridica.getId(),
                    ContaRestType.JURIDICA,
                    contaJuridica.getNumero(),
                    contaJuridica.getAgencia(),
                    contaJuridica.getStatus(),
                    contaJuridica.getSaldoInicial(),
                    contaJuridica.calcularTarifaMensal(),
                    contaJuridica.getCliente().getId(),
                    contaJuridica.getCliente().getNome(),
                    null,
                    contaJuridica.getTaxaPacoteMensal(),
                    contaJuridica.getResponsavelFinanceiro());
        }

        throw new IllegalArgumentException("Tipo de conta nao suportado.");
    }

    private void preencherClienteComum(ClienteForm form, ClienteCreateRequest request) {
        form.setTipoCliente(request.getTipo());
        form.setNome(request.getNome());
        form.setDocumento(request.getDocumento());
        form.setEmail(request.getEmail());
        form.setTelefone(request.getTelefone());
        form.setCep(request.getCep());
        form.setNumero(request.getNumero());
        form.setComplemento(request.getComplemento());
    }

    private void preencherContaComum(ContaForm form, ContaCreateRequest request) {
        form.setClienteId(request.getClienteId());
        form.setSaldoInicial(request.getSaldoInicial());
        form.setStatus(request.getStatus() == null ? ContaStatus.ATIVA : request.getStatus());
    }

    private EnderecoResponse toEnderecoResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoResponse(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf());
    }

    private void validate(Object target) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<Object> violation : validator.validate(target)) {
            fieldErrors.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage());
        }
        if (!fieldErrors.isEmpty()) {
            throw new RestValidationException("Dados invalidos.", fieldErrors);
        }
    }
}
