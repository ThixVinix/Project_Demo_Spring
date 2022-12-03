package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID>, JpaSpecificationExecutor<Pedido> {

    @Query(value = "select * from tb_pedidos where cliente_id = :clienteId", nativeQuery = true)
    List<Pedido> findAllPedidosIntoCliente(@Param("clienteId") UUID clienteId);

    @Query(value = "select * from tb_pedidos where cliente_id = :clienteId and pedido_id = :pedidoId", nativeQuery = true)
    Optional<Pedido> findPedidoIntoCliente(@Param("clienteId") UUID clienteId, @Param("pedidoId") UUID pedidoId);
	
}
