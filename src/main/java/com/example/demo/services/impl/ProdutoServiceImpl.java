package com.example.demo.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.models.Produto;
import com.example.demo.repositories.ProdutoRepository;
import com.example.demo.services.ProdutoService;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	@Autowired
	ProdutoRepository produtoRepository;
	
	@Override
	public Produto save(Produto produto) {
		return produtoRepository.save(produto);
	}

	@Override
	public Optional<Produto> findProdutoIntoPedido(UUID pedido, UUID produto) {
		return produtoRepository.findProdutoIntoPedido(pedido, produto);
	}

	@Override
	public void delete(Produto produto) {
		produtoRepository.delete(produto);
	}

	@Override
	public List<Produto> findAllByPedido(UUID pedidoId) {
		return produtoRepository.findAllProdutosIntoPedido(pedidoId);
	}

	@Override
	public Page<Produto> findAllByPedido(Specification<Produto> spec, Pageable pageable) {
		return produtoRepository.findAll(spec, pageable);
	}

}
