package com.example.demo.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.models.Pedido;
import com.example.demo.models.Produto;
import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.ProdutoRepository;
import com.example.demo.services.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@Transactional
	@Override
	public void delete(Pedido pedido) {
        List<Produto> produtos = produtoRepository.findAllProdutosIntoPedido(pedido.getPedidoId());

        if (!produtos.isEmpty()) {
            produtoRepository.deleteAll(produtos);
        }

        pedidoRepository.delete(pedido);
		
	}

	@Override
	public Pedido save(Pedido pedido) {
		return pedidoRepository.save(pedido);
	}

	@Override
	public Optional<Pedido> findPedidoIntoCliente(UUID clienteId, UUID pedidoId) {
		return pedidoRepository.findPedidoIntoCliente(clienteId, pedidoId);
	}

	@Override
	public List<Pedido> findAllByCliente(UUID clienteId) {
		return pedidoRepository.findAllPedidosIntoCliente(clienteId);
	}

	@Override
	public Optional<Pedido> findById(UUID pedidoId) {
		return pedidoRepository.findById(pedidoId);
	}

	@Override
	public Page<Pedido> findAllByCliente(Specification<Pedido> spec, Pageable pageable) {
		return pedidoRepository.findAll(spec, pageable);
	}

}
