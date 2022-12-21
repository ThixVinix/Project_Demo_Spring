package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.example.demo.dtos.PedidoDto;
import com.example.demo.enums.SituacaoUsuario;
import com.example.demo.models.Cliente;
import com.example.demo.models.Pedido;
import com.example.demo.services.ClienteService;
import com.example.demo.services.PedidoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link PedidoController}.
 */
@DisplayName("Testes do PedidoController")
@TestMethodOrder(OrderAnnotation.class)
@WebMvcTest(PedidoController.class)
@Log4j2
class PedidoControllerTest {

	private static final String TIME_ZONE = "UTC";

	private static final String VAR_COD_NOTA_FISCAL = "$.codNotaFiscal";

	private static final String COD_NOTA_FISCAL_EDITADO = "1234567890";

	private static final String NOME = "Mário Júnior";
	private static final String USERNAME = "Mario";
	private static final String SENHA = "0Qx7BGhRGF";
	private static final String EMAIL = "mario@teste.com";
	private static final String CPF = "11403113017";
	private static final String TELEFONE = "11169243579";

	private static String codNotaFiscalRandom;

	private static UUID randomUuidCliente;
	private static UUID randomUuidPedido;
	private static Cliente cliente;
	private static PedidoDto pedidoDto;
	private static Pedido pedido;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	ClienteController clienteController;

	@Autowired
	PedidoController pedidoController;

	@MockBean
	ClienteService clienteService;

	@MockBean
	PedidoService pedidoService;

	/**
	 * METODO QUE IRA EXECUTAR ANTES DE TODOS OS TESTES.
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		codNotaFiscalRandom = String.valueOf(new Random(Long.MAX_VALUE - 1).nextLong());
		randomUuidCliente = UUID.randomUUID();
		randomUuidPedido = UUID.randomUUID();
		cliente = Cliente.builder().clienteId(randomUuidCliente).nomeCompleto(NOME).username(USERNAME).password(SENHA)
				.email(EMAIL).cpf(CPF).telefone(TELEFONE).situacaoUsuario(SituacaoUsuario.ATIVO)
				.dataCriacaoRegistro(LocalDateTime.now(ZoneId.of(TIME_ZONE)))
				.dataAtualizacaoRegistro(LocalDateTime.now(ZoneId.of(TIME_ZONE))).build();
	}

	/**
	 * METODO QUE IRA EXECUTAR DEPOIS DE TODOS OS TESTES.
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * METODO QUE IRA EXECUTAR ANTES DE CADA TESTE.
	 */
	@BeforeEach
	void setUp() throws Exception {
		pedidoDto = new PedidoDto(codNotaFiscalRandom);
		pedido = Pedido.builder().pedidoId(randomUuidPedido).codNotaFiscal(codNotaFiscalRandom)
				.dataCriacao(LocalDateTime.now(ZoneId.of(TIME_ZONE))).cliente(cliente).build();
	}

	/**
	 * METODO QUE IRA EXECUTAR DEPOIS DE CADA TESTE.
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link PedidoController#salvarPedido(UUID, PedidoDto)}.
	 */
	@DisplayName("salvarPedido - Salvar pedido com sucesso")
	@Order(1)
	@Test
	void deveSalvarPedidoComSucesso() {

		BDDMockito.when(clienteService.findById(any(UUID.class))).thenReturn(Optional.of(cliente));
		BDDMockito.when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

		ResultActions result;
		try {
			result = mockMvc.perform(post("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos"))
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("POST", r.getRequest().getMethod())).andExpect(status().isCreated())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath(VAR_COD_NOTA_FISCAL, CoreMatchers.is(codNotaFiscalRandom)))
					.andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link PedidoController#salvarPedido(UUID, PedidoDto)}.
	 */
	@DisplayName("salvarPedido - Deve retornar BAD REQUEST ao enviar codNotaFiscal = NULL")
	@Order(2)
	@Test
	void deveNaoConterCodNotaFiscalAoSalvarPedido() {
		pedidoDto.setCodNotaFiscal(null);

		ResultActions result;
		try {
			result = mockMvc.perform(post("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos"))
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("POST", r.getRequest().getMethod())).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link PedidoController#salvarPedido(UUID, PedidoDto)}.
	 */
	@DisplayName("salvarPedido - Deve retornar NOT FOUND ao tentar salvar pedido")
	@Order(2)
	@Test
	void deveNaoEncontrarClienteIdAoSalvarPedido() {

		BDDMockito.when(clienteService.findById(any(UUID.class))).thenReturn(Optional.empty());

		ResultActions result;
		try {
			result = mockMvc.perform(post("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos"))
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("POST", r.getRequest().getMethod())).andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link PedidoController#deletarPedido(UUID, UUID)}.
	 */
	@DisplayName("deletarPedido - Deletar pedido com sucesso")
	@Order(3)
	@Test
	void deveDeletarClienteComSucesso() {

		BDDMockito.when(pedidoService.findPedidoIntoCliente(any(UUID.class), any(UUID.class)))
				.thenReturn(Optional.of(pedido));

		ResultActions result;
		try {
			result = mockMvc.perform(delete("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos/")
					.concat(randomUuidPedido.toString())).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("DELETE", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link PedidoController#deletarPedido(UUID, UUID)}.
	 */
	@DisplayName("deletarPedido - Deve retornar NOT FOUND ao tentar deletar pedido")
	@Order(4)
	@Test
	void deveNaoEncontrarPedidoIdAoDeletarPedido() {

		BDDMockito.when(pedidoService.findPedidoIntoCliente(any(UUID.class), any(UUID.class)))
				.thenReturn(Optional.empty());

		ResultActions result;
		try {
			result = mockMvc.perform(delete("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos/")
					.concat(randomUuidPedido.toString())).contentType(MediaType.APPLICATION_JSON));

			result.andExpect(r -> assertEquals("DELETE", r.getRequest().getMethod())).andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for
	 * {@link PedidoController#atualizarPedido(UUID, UUID, PedidoDto)}.
	 */
	@DisplayName("atualizarPedido - Deve atualizar pedido com sucesso")
	@Order(5)
	@Test
	void deveAtualizarPedidoComSucesso() {

		pedidoDto.setCodNotaFiscal(COD_NOTA_FISCAL_EDITADO);
		BDDMockito.when(pedidoService.findPedidoIntoCliente(any(UUID.class), any(UUID.class)))
				.thenReturn(Optional.of(pedido));
		BDDMockito.when(pedidoService.save(any(Pedido.class))).thenReturn(pedido);

		ResultActions result;
		try {
			result = mockMvc.perform(put("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos/")
					.concat(randomUuidPedido.toString())).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("PUT", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath(VAR_COD_NOTA_FISCAL, CoreMatchers.is(COD_NOTA_FISCAL_EDITADO)))
					.andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for
	 * {@link PedidoController#atualizarPedido(UUID, UUID, PedidoDto)}.
	 */
	@DisplayName("atualizarPedido - Deve retornar NOT FOUND ao tentar atualizar pedido")
	@Order(5)
	@Test
	void deveNaoEncontrarClienteIdAoAtualizarPedido() {

		pedidoDto.setCodNotaFiscal(COD_NOTA_FISCAL_EDITADO);
		BDDMockito.when(pedidoService.findPedidoIntoCliente(any(UUID.class), any(UUID.class)))
				.thenReturn(Optional.empty());

		ResultActions result;
		try {
			result = mockMvc.perform(put("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos/")
					.concat(randomUuidPedido.toString())).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(pedidoDto)));

			result.andExpect(r -> assertEquals("PUT", r.getRequest().getMethod())).andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}

	/**
	 * Test method for
	 * {@link PedidoController#getAllPedidos(com.example.demo.specifications.SpecificationTemplate.PedidoSpec, org.springframework.data.domain.Pageable, UUID)}.
	 */
	@DisplayName("getAllPedidos - Deve retornar todos os pedidos existentes")
	@Order(7)
	@Test
	void deveRetornarTodosPedidosComSucesso() {

		List<Pedido> pedidos = Arrays.asList(pedido, pedido, pedido);
		Page<Pedido> pagedResponse = new PageImpl<>(pedidos);
		BDDMockito.when(pedidoService.findAllByCliente(any(), any())).thenReturn(pagedResponse);

		ResultActions result;
		try {
			result = mockMvc.perform(get("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos"))
					.contentType(MediaType.APPLICATION_JSON));

			result.andExpect(r -> assertEquals("GET", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}
	
	/**
	 * Test method for
	 * {@link PedidoController#getAllPedidos(com.example.demo.specifications.SpecificationTemplate.PedidoSpec, org.springframework.data.domain.Pageable, UUID)}.
	 */
	@DisplayName("getAllPedidos - Deve retornar lista VAZIA de pedidos")
	@Order(8)
	@Test
	void deveRetornarListaVaziaClientes() {
		BDDMockito.when(pedidoService.findAllByCliente(any(), any())).thenReturn(Page.empty());

		ResultActions result;
		try {
			result = mockMvc.perform(get("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos")).contentType(MediaType.APPLICATION_JSON));
			result.andExpect(r -> assertEquals("GET", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}
	
	/**
	 * Test method for {@link PedidoController#getOnePedido(UUID, UUID)}.
	 */
	@DisplayName("getOnePedido - Deve retornar pedido com sucesso")
	@Order(9)
	@Test
	void deveRetornarClienteComSucesso() {

		BDDMockito.when(pedidoService.findPedidoIntoCliente(randomUuidCliente, randomUuidPedido)).thenReturn(Optional.of(pedido));

		ResultActions result;
		try {
			result = mockMvc
					.perform(get("/clientes/".concat(randomUuidCliente.toString()).concat("/pedidos/").concat(randomUuidPedido.toString())).contentType(MediaType.APPLICATION_JSON));
			result.andExpect(r -> assertEquals("GET", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}
}
