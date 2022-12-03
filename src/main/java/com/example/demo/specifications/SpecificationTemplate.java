package com.example.demo.specifications;

import java.util.Collection;
import java.util.UUID;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Cliente;
import com.example.demo.models.Pedido;
import com.example.demo.models.Produto;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

public class SpecificationTemplate {

    @And({
        @Spec(path = "telefone", spec = Equal.class),
        @Spec(path = "clienteId", spec = Equal.class),
        @Spec(path = "nomeCompleto", spec = Like.class),
        @Spec(path = "username", spec = Like.class),
        @Spec(path = "email", spec = Like.class),
        @Spec(path = "cpf", spec = Like.class)
})
public interface ClienteSpec extends Specification<Cliente> {
}
    
    @And({
    	@Spec(path = "codNotaFiscal", spec = Equal.class)
})
    public interface PedidoSpec extends Specification<Pedido> {
    }
	
    @And({
    	@Spec(path = "situacaoPedido", spec = Equal.class),
    	@Spec(path = "codNotaFiscal", spec = Equal.class),
    	@Spec(path = "nome", spec = Like.class),
})
    public interface ProdutoSpec extends Specification<Produto> {
    }
    
    public static Specification<Pedido> pedidoClienteId(final UUID clienteId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<Pedido> pedido = root;
            Root<Cliente> cliente = query.from(Cliente.class);
            Expression<Collection<Pedido>> clientesPedidos = cliente.get("pedidos");
            return cb.and(cb.equal(cliente.get("clienteId"), clienteId), cb.isMember(pedido, clientesPedidos));
        };
    }
    
    public static Specification<Produto> produtoPedidoId(final UUID pedidoId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<Produto> produto = root;
            Root<Pedido> pedido = query.from(Pedido.class);
            Expression<Collection<Produto>> pedidosProdutos = pedido.get("produtos");
            return cb.and(cb.equal(pedido.get("pedidoId"), pedidoId), cb.isMember(produto, pedidosProdutos));
        };
    }
}
