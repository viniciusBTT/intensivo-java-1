package com.intensivo.java.repository.clientes;

import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.TipoCliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByOrderByIdDesc();

    List<Cliente> findAllByTipoClienteOrderByNomeAsc(TipoCliente tipoCliente);

    boolean existsByDocumento(String documento);

    boolean existsByDocumentoAndIdNot(String documento, Long id);
}
