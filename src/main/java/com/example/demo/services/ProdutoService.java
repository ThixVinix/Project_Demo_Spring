package com.example.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Produto;

public interface ProdutoService {

    Produto save(Produto produto);

    Optional<Produto> findProdutoIntoPedido(UUID pedido, UUID produto);

    void delete(Produto produto);

    List<Produto> findAllByPedido(UUID pedidoId);

    Page<Produto> findAllByPedido(Specification<Produto> spec, Pageable pageable);
	
}
