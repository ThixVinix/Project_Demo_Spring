package com.example.demo.controllers;

import static com.example.demo.constants.DescriptionGlobalConstant.BAD_REQUEST_MESSAGE;
import static com.example.demo.constants.DescriptionGlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.example.demo.constants.DescriptionPedidoConstant.CREATED_PEDIDO;
import static com.example.demo.constants.DescriptionPedidoConstant.DELETED_PEDIDO;
import static com.example.demo.constants.DescriptionPedidoConstant.EDITED_PEDIDO;
import static com.example.demo.constants.DescriptionPedidoConstant.FOUND_PEDIDO;
import static com.example.demo.constants.DescriptionPedidoConstant.FOUND_PEDIDOS;
import static com.example.demo.constants.DescriptionPedidoConstant.NOT_FOUND_PEDIDO;
import static com.example.demo.constants.DescriptionPedidoConstant.NOT_FOUND_PEDIDO_CLIENTE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.demo.dtos.PedidoDto;
import com.example.demo.enums.SituacaoPedido;
import com.example.demo.models.Cliente;
import com.example.demo.models.Pedido;
import com.example.demo.services.ClienteService;
import com.example.demo.services.PedidoService;
import com.example.demo.specifications.SpecificationTemplate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;

@Api(value = "API REST Pedidos")
@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

	@Autowired
	PedidoService pedidoService;

	@Autowired
	ClienteService clienteService;

	@ApiOperation(notes = "Endpoint para criar um novo pedido", value = "Cria um novo pedido", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 201, message = CREATED_PEDIDO, response = Pedido.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PostMapping(value = "/clientes/{clienteId}/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> salvarPedido(@PathVariable(value = "clienteId") UUID clienteId,
			@RequestBody @Valid PedidoDto pedidoDto) {

		Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

		if (clienteOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_PEDIDO_CLIENTE);
		}

		var pedido = new Pedido();

		BeanUtils.copyProperties(pedidoDto, pedido);
		pedido.setSituacaoPedido(SituacaoPedido.SOLICITADO);
		pedido.setDataCriacao(LocalDateTime.now(ZoneId.of("UTC")));
		pedido.setCliente(clienteOptional.get());
		pedidoService.save(pedido);
		log.debug("POST salvarPedido - pedidoId salvo: {} ", pedido.getPedidoId());
		log.info("Pedido salvo com sucesso - pedidoId: {} ", pedido.getPedidoId());
		return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
	}

	@ApiOperation(notes = "Endpoint para deletar um pedido", value = "Deleta um pedido", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = DELETED_PEDIDO, response = Pedido.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PEDIDO_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@DeleteMapping(value = "/clientes/{clienteId}/pedidos/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deletarPedido(@PathVariable(value = "clienteId") UUID clienteId,
			@PathVariable(value = "pedidoId") UUID pedidoId) {
		log.debug("DELETE deletarPedido - pedidoId recebido: {} ", pedidoId);
		Optional<Pedido> pedidoOptional = pedidoService.findPedidoIntoCliente(clienteId, pedidoId);
		if (pedidoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_PEDIDO_CLIENTE);
		}

		pedidoService.delete(pedidoOptional.get());
		log.debug("DELETE deletarPedido - pedidoId deletado: {} ", pedidoId);
		log.info("Pedido deletado com sucesso - pedidoId: {} ", pedidoId);
		return ResponseEntity.ok().body("Pedido deletado com sucesso.");
	}

	@ApiOperation(notes = "Endpoint para atualizar um pedido", value = "Atualiza um pedido", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = EDITED_PEDIDO, response = Pedido.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PEDIDO_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PutMapping(value = "/clientes/{clienteId}/pedidos/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> atualizarPedido(@PathVariable(value = "clienteId") UUID clienteId,
			@PathVariable(value = "pedidoId") UUID pedidoId, @RequestBody @Valid PedidoDto pedidoDto) {
		log.debug("PUT atualizarPedido - pedidoDto recebido: {} ", pedidoDto.toString());
		Optional<Pedido> pedidoOptional = pedidoService.findPedidoIntoCliente(clienteId, pedidoId);

		if (pedidoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_PEDIDO_CLIENTE);
		}

		var pedido = pedidoOptional.get();
		pedido.setCodNotaFiscal(pedidoDto.getCodNotaFiscal());
		pedidoService.save(pedido);
		log.debug("PUT atualizarPedido - pedidoId atualizado: {} ", pedido.getPedidoId());
		log.info("Pedido atualizado com sucesso - pedidoId: {} ", pedido.getPedidoId());
		return ResponseEntity.ok().body(pedido);
	}

	@ApiOperation(notes = "Endpoint para visualizar todos os pedidos", value = "Visualiza todos os pedidos", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_PEDIDOS, response = Pedido.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PEDIDO, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping(value = "/clientes/{clienteId}/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Pedido>> getAllPedidos(SpecificationTemplate.PedidoSpec spec,
			@PageableDefault(page = 0, size = 10, sort = "pedidoId", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(value = "clienteId") UUID clienteId) {
		return ResponseEntity.ok(pedidoService.findAllByCliente(SpecificationTemplate.pedidoClienteId(clienteId).and(spec), pageable));
				
	}

	@ApiOperation(notes = "Endpoint para visualizar um determinado pedido", value = "Visualiza um determinado pedido", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_PEDIDO, response = Pedido.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_PEDIDO_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping(value = "/clientes/{clienteId}/pedidos/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getOnePedido(@PathVariable(value = "clienteId") UUID clienteId,
			@PathVariable(value = "pedidoId") UUID pedidoId) {

		Optional<Pedido> pedidoOptional = pedidoService.findPedidoIntoCliente(clienteId, pedidoId);

		if (pedidoOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_PEDIDO_CLIENTE);
		}

		return ResponseEntity.ok().body(pedidoOptional.get());
	}

}
