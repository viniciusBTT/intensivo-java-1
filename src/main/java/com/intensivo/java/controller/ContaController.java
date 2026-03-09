package com.intensivo.java.controller;

import com.intensivo.java.dto.form.ContaCorrenteForm;
import com.intensivo.java.dto.form.ContaJuridicaForm;
import com.intensivo.java.exception.BusinessException;
import com.intensivo.java.model.ContaCorrente;
import com.intensivo.java.model.ContaJuridica;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping("/contas")
    public String listar(Model model) {
        model.addAttribute("contas", contaService.listarTodas());
        return "contas/list";
    }

    @GetMapping("/contas/corrente/novo")
    public String novaContaCorrente(Model model) {
        prepararFormularioCorrente(model, new ContaCorrenteForm(), false);
        return "contas/corrente-form";
    }

    @PostMapping("/contas/corrente")
    public String criarContaCorrente(@Valid @ModelAttribute("form") ContaCorrenteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioCorrente(model, form, false);
            return "contas/corrente-form";
        }
        try {
            contaService.criarContaCorrente(form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta corrente criada com sucesso.");
            return "redirect:/contas";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioCorrente(model, form, false);
            return "contas/corrente-form";
        }
    }

    @GetMapping("/contas/corrente/{id}/editar")
    public String editarContaCorrente(@PathVariable Long id, Model model) {
        ContaCorrente conta = contaService.buscarContaCorrente(id);
        prepararFormularioCorrente(model, toCorrenteForm(conta), true);
        model.addAttribute("numeroConta", conta.getNumero());
        model.addAttribute("agenciaConta", conta.getAgencia());
        return "contas/corrente-form";
    }

    @PostMapping("/contas/corrente/{id}")
    public String atualizarContaCorrente(@PathVariable Long id,
            @Valid @ModelAttribute("form") ContaCorrenteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioCorrente(model, form, true);
            return "contas/corrente-form";
        }
        try {
            contaService.atualizarContaCorrente(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta corrente atualizada com sucesso.");
            return "redirect:/contas";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioCorrente(model, form, true);
            return "contas/corrente-form";
        }
    }

    @GetMapping("/contas/juridica/novo")
    public String novaContaJuridica(Model model) {
        prepararFormularioJuridica(model, new ContaJuridicaForm(), false);
        return "contas/juridica-form";
    }

    @PostMapping("/contas/juridica")
    public String criarContaJuridica(@Valid @ModelAttribute("form") ContaJuridicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioJuridica(model, form, false);
            return "contas/juridica-form";
        }
        try {
            contaService.criarContaJuridica(form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta juridica criada com sucesso.");
            return "redirect:/contas";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioJuridica(model, form, false);
            return "contas/juridica-form";
        }
    }

    @GetMapping("/contas/juridica/{id}/editar")
    public String editarContaJuridica(@PathVariable Long id, Model model) {
        ContaJuridica conta = contaService.buscarContaJuridica(id);
        prepararFormularioJuridica(model, toJuridicaForm(conta), true);
        model.addAttribute("numeroConta", conta.getNumero());
        model.addAttribute("agenciaConta", conta.getAgencia());
        return "contas/juridica-form";
    }

    @PostMapping("/contas/juridica/{id}")
    public String atualizarContaJuridica(@PathVariable Long id,
            @Valid @ModelAttribute("form") ContaJuridicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioJuridica(model, form, true);
            return "contas/juridica-form";
        }
        try {
            contaService.atualizarContaJuridica(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta juridica atualizada com sucesso.");
            return "redirect:/contas";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioJuridica(model, form, true);
            return "contas/juridica-form";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/contas/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contaService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Conta excluida com sucesso.");
        return "redirect:/contas";
    }

    private void prepararFormularioCorrente(Model model, ContaCorrenteForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", edicao);
        model.addAttribute("submitPath", edicao ? "/contas/corrente/" + form.getId() : "/contas/corrente");
        model.addAttribute("tituloFormulario", edicao ? "Editar Conta Corrente" : "Nova Conta Corrente");
        model.addAttribute("tipoContaLabel", "Conta Corrente");
        model.addAttribute("clientes", contaService.listarClientesCorrente());
        model.addAttribute("statusList", ContaStatus.values());
    }

    private void prepararFormularioJuridica(Model model, ContaJuridicaForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", edicao);
        model.addAttribute("submitPath", edicao ? "/contas/juridica/" + form.getId() : "/contas/juridica");
        model.addAttribute("tituloFormulario", edicao ? "Editar Conta Juridica" : "Nova Conta Juridica");
        model.addAttribute("tipoContaLabel", "Conta Juridica");
        model.addAttribute("clientes", contaService.listarClientesJuridicos());
        model.addAttribute("statusList", ContaStatus.values());
    }

    private ContaCorrenteForm toCorrenteForm(ContaCorrente conta) {
        ContaCorrenteForm form = new ContaCorrenteForm();
        form.setId(conta.getId());
        form.setClienteId(conta.getCliente().getId());
        form.setSaldoInicial(conta.getSaldoInicial());
        form.setStatus(conta.getStatus());
        form.setLimiteChequeEspecial(conta.getLimiteChequeEspecial());
        return form;
    }

    private ContaJuridicaForm toJuridicaForm(ContaJuridica conta) {
        ContaJuridicaForm form = new ContaJuridicaForm();
        form.setId(conta.getId());
        form.setClienteId(conta.getCliente().getId());
        form.setSaldoInicial(conta.getSaldoInicial());
        form.setStatus(conta.getStatus());
        form.setTaxaPacoteMensal(conta.getTaxaPacoteMensal());
        form.setResponsavelFinanceiro(conta.getResponsavelFinanceiro());
        return form;
    }
}
