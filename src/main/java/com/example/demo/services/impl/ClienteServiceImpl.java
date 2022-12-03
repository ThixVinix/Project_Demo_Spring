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

import com.example.demo.models.Cliente;
import com.example.demo.models.Pedido;
import com.example.demo.models.Produto;
import com.example.demo.repositories.ClienteRepository;
import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.ProdutoRepository;
import com.example.demo.services.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@Transactional
	@Override
	public void delete(Cliente cliente) {
		List<Pedido> pedidos = pedidoRepository.findAllPedidosIntoCliente(cliente.getClienteId());

		if (!pedidos.isEmpty()) {
			for (Pedido pedido : pedidos) {
				List<Produto> produtos = produtoRepository.findAllProdutosIntoPedido(pedido.getPedidoId());
				if (!produtos.isEmpty()) {
					produtoRepository.deleteAll(produtos);
				}
			}
			pedidoRepository.deleteAll(pedidos);
		}

		clienteRepository.delete(cliente);
	}

	@Override
	public Cliente save(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	@Override
	public Optional<Cliente> findById(UUID clienteId) {
		return clienteRepository.findById(clienteId);
	}

	@Override
	public Page<Cliente> findAll(Specification<Cliente> spec, Pageable pageable) {
		return clienteRepository.findAll(spec, pageable);
	}

	

	
}
