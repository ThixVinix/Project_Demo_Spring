package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID>, JpaSpecificationExecutor<Produto> {

	@Query(value = "select * from tb_produtos where pedido_id = :pedidoId", nativeQuery = true)
	List<Produto> findAllProdutosIntoPedido(@Param("pedidoId") UUID pedidoId);

	@Query(value = "select * from tb_produtos where pedido_id = :pedidoId and ´produto_id = :produtoId", nativeQuery = true)
	Optional<Produto> findProdutoIntoPedido(@Param("pedidoId") UUID pedidoId, @Param("produtoId") UUID produtoId);

}
