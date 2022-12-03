package com.example.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Pedido;

public interface PedidoService {

    void delete(Pedido pedido);

    Pedido save(Pedido pedido);

    Optional<Pedido> findPedidoIntoCliente(UUID clienteId, UUID pedidoId);

    List<Pedido> findAllByCliente(UUID clienteId);

    Optional<Pedido> findById(UUID pedidoId);

    Page<Pedido> findAllByCliente(Specification<Pedido> spec, Pageable pageable);
	
}
