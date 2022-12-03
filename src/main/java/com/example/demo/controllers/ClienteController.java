package com.example.demo.controllers;

import static com.example.demo.constants.DescriptionClienteConstant.CREATED_CLIENTE;
import static com.example.demo.constants.DescriptionClienteConstant.DELETED_CLIENTE;
import static com.example.demo.constants.DescriptionClienteConstant.EDITED_CLIENTE;
import static com.example.demo.constants.DescriptionClienteConstant.FOUND_CLIENTE;
import static com.example.demo.constants.DescriptionClienteConstant.FOUND_CLIENTES;
import static com.example.demo.constants.DescriptionClienteConstant.NOT_FOUND_CLIENTE;
import static com.example.demo.constants.DescriptionGlobalConstant.BAD_REQUEST_MESSAGE;
import static com.example.demo.constants.DescriptionGlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE;

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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.example.demo.dtos.ClienteDto;
import com.example.demo.enums.SituacaoUsuario;
import com.example.demo.models.Cliente;
import com.example.demo.services.ClienteService;
import com.example.demo.specifications.SpecificationTemplate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;

@Api(value = "API REST Clientes")
@Log4j2
@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

	@Autowired
	ClienteService clienteService;

	@ApiOperation(notes = "Endpoint para criar um novo cliente", value = "Cria um novo cliente", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 201, message = CREATED_CLIENTE, response = Cliente.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> salvarCliente(@Valid @RequestBody ClienteDto clienteDto, Errors errors) {
		log.debug("POST salvarCliente - clienteDto recebido: {} ", clienteDto.toString());
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors());
		}

		var cliente = new Cliente();

		BeanUtils.copyProperties(clienteDto, cliente);
		cliente.setSituacaoUsuario(SituacaoUsuario.ATIVO);
		cliente.setDataCriacaoRegistro(LocalDateTime.now(ZoneId.of("UTC")));
		cliente.setDataAtualizacaoRegistro(LocalDateTime.now(ZoneId.of("UTC")));
		clienteService.save(cliente);
		log.debug("POST salvarCliente - clienteId salvo: {} ", cliente.getClienteId());
		log.info("Cliente salvo com sucesso - clienteId: {} ", cliente.getClienteId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
	}
	
	@ApiOperation(notes = "Endpoint para deletar um cliente", value = "Deleta um cliente", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = DELETED_CLIENTE, response = Cliente.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@DeleteMapping(value = "/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> deletarCliente(@PathVariable(value = "clienteId") UUID clienteId) {
		log.debug("DELETE deletarCliente - clienteId recebido: {} ", clienteId);
		Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

		if (clienteOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_CLIENTE);
		}

		clienteService.delete(clienteOptional.get());
		log.debug("DELETE deletarCliente - clienteId deletado: {} ", clienteId);
		log.info("Cliente deletado com sucesso - clienteId: {} ", clienteId);
		return ResponseEntity.ok(DELETED_CLIENTE);
	}

	@ApiOperation(notes = "Endpoint para alterar um cliente", value = "Altera um cliente", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = EDITED_CLIENTE, response = Cliente.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@PutMapping(value = "/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> atualizarCliente(@PathVariable(value = "clienteId") UUID clienteId,
			@RequestBody @Valid ClienteDto clienteDto) {
		log.debug("PUT atualizarCliente - clienteDto recebido: {} ", clienteDto.toString());
		Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

		if (clienteOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_CLIENTE);
		}

		var cliente = clienteOptional.get();
		cliente.setNomeCompleto(clienteDto.getNomeCompleto());
		cliente.setUsername(clienteDto.getUsername());
		cliente.setPassword(clienteDto.getPassword());
		cliente.setEmail(clienteDto.getEmail());
		cliente.setDataAtualizacaoRegistro(LocalDateTime.now(ZoneId.of("UTC")));
		log.debug("PUT atualizarCliente - clienteId atualizado: {} ", cliente.getClienteId());
		log.info("Cliente atualizado com sucesso - clienteId {} ", cliente.getClienteId());
		return ResponseEntity.ok().body(clienteService.save(cliente));
	}

	@ApiOperation(notes = "Endpoint para visualizar todos os cliente", value = "Visualiza todos os clientes", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_CLIENTES, response = Cliente.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Page<Cliente>> getAllClientes(SpecificationTemplate.ClienteSpec spec,
			@PageableDefault(page = 0, size = 10, sort = "clienteId", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok().body(clienteService.findAll(spec, pageable));
	}

	@ApiOperation(notes = "Endpoint para visualizar um determinado cliente", value = "Visualiza um determinado cliente", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses({ @ApiResponse(code = 200, message = FOUND_CLIENTE, response = Cliente.class),
			@ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = BadRequest.class),
			@ApiResponse(code = 404, message = NOT_FOUND_CLIENTE, response = NotFound.class),
			@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR_MESSAGE, response = InternalServerError.class) })
	@GetMapping(value = "/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getOneCliente(@PathVariable(value = "clienteId") UUID clienteId) {

		Optional<Cliente> clienteOptional = clienteService.findById(clienteId);

		if (clienteOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NOT_FOUND_CLIENTE);
		}

		return ResponseEntity.ok().body(clienteOptional.get());
	}

}
