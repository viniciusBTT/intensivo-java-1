package com.intensivo.java.controller;

import com.intensivo.java.dto.ClienteDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @GetMapping("/api/hello")
    public String helloWorld() {
        System.out.println("hello world");
        return "hello world";
    }

    @PostMapping({"/api/clientes", "/api/users"})
    public ClienteDto receberCliente(@RequestBody ClienteDto clienteDto) {
        System.out.println("Cliente recebido via JSON: " + clienteDto);
        return clienteDto;
    }
}
