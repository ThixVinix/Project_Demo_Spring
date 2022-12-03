package com.example.demo.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Cliente;

public interface ClienteService {
	
    void delete(Cliente cliente);

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(UUID clienteId);

    Page<Cliente> findAll(Specification<Cliente> spec, Pageable pageable);
}
