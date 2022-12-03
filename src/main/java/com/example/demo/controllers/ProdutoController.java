package com.example.demo.controllers;

import static com.example.demo.constants.DescriptionGlobalConstant.BAD_REQUEST_MESSAGE;
import static com.example.demo.constants.DescriptionGlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.example.demo.constants.DescriptionProdutoConstant.CREATED_PRODUTO;
import static com.example.demo.constants.DescriptionProdutoConstant.DELETED_PRODUTO;
import static com.example.demo.constants.DescriptionProdutoConstant.EDITED_PRODUTO;
import static com.example.demo.constants.DescriptionProdutoConstant.FOUND_PRODUTO;
import static com.example.demo.constants.DescriptionProdutoConstant.FOUND_PRODUTOS;
import static com.example.demo.constants.DescriptionProdutoConstant.NOT_FOUND_PRODUTO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.example.demo.dtos.ProdutoDto;
import com.example.demo.models.Pedido;
import com.example.demo.models.Produto;
import com.example.demo.services.PedidoService;
import com.example.demo.services.ProdutoService;
import com.example.demo.specifications.SpecificationTemplate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;

@Api(value = "API REST Produtos")
@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProdutoController {

	private static final String PRODUTO_NAO_ENCONTRADO = "Produto não encontrado.";

	@Autowired
	ProdutoService produtoService;

	@Autowired
	PedidoService pedidoService;

	@ApiOperation(notes = "Endpoint para criar um novo produto", value = "Cria um novo produto", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 201, message = CREATED_PRODUTO, response = Produto.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PostMapping("/pedidos/{pedidoId}/produtos")
	public ResponseEntity<Object> salvarProduto(@PathVariable(value = "pedidoId") UUID pedidoId,
			@RequestBody @Valid ProdutoDto produtoDto) {
		log.debug("POST salvarProduto - produtoDto recebido: {} ", produtoDto.toString());
		Optional<Pedido> pedidoOptional = pedidoService.findById(pedidoId);

		if (pedidoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUTO_NAO_ENCONTRADO);
		}

		var produto = new Produto();

		BeanUtils.copyProperties(produtoDto, produto);
		produto.setDataCriacao(LocalDateTime.now(ZoneId.of("UTC")));
		produto.setPedido(pedidoOptional.get());
		log.debug("POST salvarProduto - produtoId saved {} ", produto.getProdutoId());
		log.info("Produto salvo com sucesso - produtoId: {} ", produto.getProdutoId());
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.save(produto));
	}

	@ApiOperation(notes = "Endpoint para deletar um produto", value = "Deleta um produto", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = DELETED_PRODUTO, response = Produto.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PRODUTO, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@DeleteMapping("/pedidos/{pedidoId}/produtos/{produtoId}")
	public ResponseEntity<Object> deletarProduto(@PathVariable(value = "pedidoId") UUID pedidoId,
			@PathVariable(value = "produtoId") UUID produtoId) {
		log.debug("DELETE deletarProduto - produtoId recebido: {} ", produtoId);
		Optional<Produto> produtoOptional = produtoService.findProdutoIntoPedido(pedidoId, produtoId);

		if (produtoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUTO_NAO_ENCONTRADO);
		}

		produtoService.delete(produtoOptional.get());
		log.debug("DELETE deletarProduto - produtoId deletado: {} ", produtoId);
		log.info("Produto deletado com sucesso - produtoId: {} ", produtoId);
		return ResponseEntity.ok().body("Produto deletado com sucesso");
	}

	@ApiOperation(notes = "Endpoint para alterar um produto", value = "Altera um produto", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = EDITED_PRODUTO, response = Produto.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PRODUTO, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PutMapping("/pedidos/{pedidoId}/produtos/{produtoId}")
	public ResponseEntity<Object> atualizarProduto(@PathVariable(value = "pedidoId") UUID pedidoId,
			@PathVariable(value = "produtoId") UUID produtoId, @RequestBody @Valid ProdutoDto produtoDto) {
		log.debug("PUT atualizarProduto - produtoDto recebido: {} ", produtoDto.toString());
		Optional<Produto> produtoOptional = produtoService.findProdutoIntoPedido(pedidoId, produtoId);

		if (produtoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUTO_NAO_ENCONTRADO);
		}

		var produto = produtoOptional.get();
		produto.setDataAtualizacao(LocalDateTime.now(ZoneId.of("UTC")));
		produto.setNome(produtoDto.getNome());
		produto.setDescricao(produtoDto.getDescricao());
		produto.setValor(produtoDto.getValor());
		produto.setImagemProduto(produtoDto.getImagemProduto());
		log.debug("PUT atualizarProduto - produtoId atualizado: {} ", produto.getProdutoId());
		log.info("Produto atualizado com sucesso - produtoId: {} ", produto.getProdutoId());
		return ResponseEntity.ok().body(produtoService.save(produto));
	}

	@ApiOperation(notes = "Endpoint para visualizar todos os produtos", value = "Visualiza todos os produtos", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_PRODUTOS, response = Produto.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PRODUTO, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping("/pedidos/{pedidoId}/produtos")
	public ResponseEntity<Page<Produto>> getAllPedidos(SpecificationTemplate.ProdutoSpec spec,
			@PageableDefault(page = 0, size = 10, sort = "produtoId", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(value = "pedidoId") UUID pedidoId) {
		return ResponseEntity.ok().body(
				produtoService.findAllByPedido(SpecificationTemplate.produtoPedidoId(pedidoId).and(spec), pageable));
	}

	@ApiOperation(notes = "Endpoint para visualizar um determinado produto", value = "Visualiza um determinado produto", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_PRODUTO, response = Produto.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PRODUTO, response = NotFoundException.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping("/pedidos/{pedidoId}/produtos/{produtoId}")
	public ResponseEntity<Object> getOnePedido(@PathVariable(value = "pedidoId") UUID pedidoId,
			@PathVariable(value = "produtoId") UUID produtoId) {

		Optional<Produto> produtoOptional = produtoService.findProdutoIntoPedido(pedidoId, produtoId);

		if (produtoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUTO_NAO_ENCONTRADO);
		}

		return ResponseEntity.ok().body(produtoOptional.get());
	}

}
