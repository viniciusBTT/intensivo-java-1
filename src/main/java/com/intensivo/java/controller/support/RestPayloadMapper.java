package com.intensivo.java.controller.support;

import com.intensivo.java.dto.form.ClientePessoaFisicaForm;
import com.intensivo.java.dto.form.ClientePessoaJuridicaForm;
import com.intensivo.java.dto.form.ContaCorrenteForm;
import com.intensivo.java.dto.form.ContaJuridicaForm;
import com.intensivo.java.dto.rest.ClienteCreateRequest;
import com.intensivo.java.dto.rest.ClienteResponse;
import com.intensivo.java.dto.rest.ClienteRestType;
import com.intensivo.java.dto.rest.ContaCreateRequest;
import com.intensivo.java.dto.rest.ContaResponse;
import com.intensivo.java.dto.rest.ContaRestType;
import com.intensivo.java.dto.rest.EnderecoResponse;
import com.intensivo.java.exception.RestValidationException;
import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.model.Conta;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.model.Endereco;
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

    public ClientePessoaFisicaForm toPessoaFisicaForm(ClienteCreateRequest request) {
        ClientePessoaFisicaForm form = new ClientePessoaFisicaForm();
        preencherClienteComum(form, request);
        form.setNomeCompleto(request.getNomeCompleto());
        form.setCpf(request.getCpf());
        validate(form);
        return form;
    }

    public ClientePessoaJuridicaForm toPessoaJuridicaForm(ClienteCreateRequest request) {
        ClientePessoaJuridicaForm form = new ClientePessoaJuridicaForm();
        preencherClienteComum(form, request);
        form.setRazaoSocial(request.getRazaoSocial());
        form.setNomeFantasia(request.getNomeFantasia());
        form.setCnpj(request.getCnpj());
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
        if (cliente instanceof ClientePessoaFisica pessoaFisica) {
            return new ClienteResponse(
                    pessoaFisica.getId(),
                    ClienteRestType.PF,
                    pessoaFisica.getNomeExibicao(),
                    pessoaFisica.getDocumento(),
                    pessoaFisica.getEmail(),
                    pessoaFisica.getTelefone(),
                    toEnderecoResponse(pessoaFisica.getEndereco()),
                    pessoaFisica.getNomeCompleto(),
                    pessoaFisica.getCpf(),
                    null,
                    null,
                    null);
        }

        if (cliente instanceof ClientePessoaJuridica pessoaJuridica) {
            return new ClienteResponse(
                    pessoaJuridica.getId(),
                    ClienteRestType.PJ,
                    pessoaJuridica.getNomeExibicao(),
                    pessoaJuridica.getDocumento(),
                    pessoaJuridica.getEmail(),
                    pessoaJuridica.getTelefone(),
                    toEnderecoResponse(pessoaJuridica.getEndereco()),
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getNomeFantasia(),
                    pessoaJuridica.getCnpj());
        }

        throw new IllegalArgumentException("Tipo de cliente nao suportado.");
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
                    contaCorrente.getCliente().getNomeExibicao(),
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
                    contaJuridica.getCliente().getNomeExibicao(),
                    null,
                    contaJuridica.getTaxaPacoteMensal(),
                    contaJuridica.getResponsavelFinanceiro());
        }

        throw new IllegalArgumentException("Tipo de conta nao suportado.");
    }

    private void preencherClienteComum(com.intensivo.java.dto.form.ClienteForm form, ClienteCreateRequest request) {
        form.setEmail(request.getEmail());
        form.setTelefone(request.getTelefone());
        form.setCep(request.getCep());
        form.setNumero(request.getNumero());
        form.setComplemento(request.getComplemento());
    }

    private void preencherContaComum(com.intensivo.java.dto.form.ContaForm form, ContaCreateRequest request) {
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
