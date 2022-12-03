package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.example.demo.dtos.ClienteDto;
import com.example.demo.enums.SituacaoUsuario;
import com.example.demo.models.Cliente;
import com.example.demo.services.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link ClienteController}.
 */
@DisplayName("Testes do ClienteController")
@TestMethodOrder(OrderAnnotation.class)
@WebMvcTest(ClienteController.class)
@Log4j2
class ClienteControllerTest {

	private static final String TIME_ZONE = "UTC";

	private static final String VAR_NOME_COMPLETO = "$.nomeCompleto";
	private static final String VAR_USERNAME = "$.username";
	private static final String VAR_EMAIL = "$.email";
	private static final String VAR_CPF = "$.cpf";
	private static final String VAR_TELEFONE = "$.telefone";
	private static final String VAR_SITUACAO_USUARIO = "$.situacaoUsuario";
	private static final String VAR_DATA_CRIACAO = "$.dataCriacaoRegistro";
	private static final String VAR_DATA_ATUALIZACAO = "$.dataAtualizacaoRegistro";

	private static final String NOME = "Mário Júnior";
	private static final String USERNAME = "Mario";
	private static final String SENHA = "0Qx7BGhRGF";
	private static final String EMAIL = "mario@teste.com";
	private static final String CPF = "11403113017";
	private static final String TELEFONE = "11169243579";

	private static UUID randomUuid;
	private static ClienteDto clienteDto;
	private static Cliente cliente;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ClienteController clienteController;

	@MockBean
	ClienteService clienteService;

	/**
	 * METODO QUE IRA EXECUTAR ANTES DE TODOS OS TESTES.
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		randomUuid = UUID.randomUUID();
		cliente = Cliente.builder().clienteId(UUID.randomUUID()).nomeCompleto(NOME).username(USERNAME).password(SENHA)
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
		clienteDto = new ClienteDto(NOME, USERNAME, SENHA, EMAIL, CPF, TELEFONE);
	}

	/**
	 * METODO QUE IRA EXECUTAR DEPOIS DE CADA TESTE.
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link ClienteController#salvarCliente(ClienteDto, org.springframework.validation.Errors)}.
	 */
	@DisplayName("salvarCliente - Salvar cliente com sucesso")
	@Order(1)
	@Test
	void deveSalvarClienteComSucesso() {

		BDDMockito.when(clienteService.save(cliente)).thenReturn(cliente);

		ResultActions result;
		try {
			result = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(clienteDto)));

			result.andExpect(r -> assertEquals("POST", r.getRequest().getMethod())).andExpect(status().isCreated())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpectAll(jsonPath(VAR_NOME_COMPLETO, CoreMatchers.is(NOME)),
							jsonPath(VAR_USERNAME, CoreMatchers.is(USERNAME)),
							jsonPath(VAR_EMAIL, CoreMatchers.is(EMAIL)), jsonPath(VAR_CPF, CoreMatchers.is(CPF)),
							jsonPath(VAR_TELEFONE, CoreMatchers.is(TELEFONE)),
							jsonPath(VAR_SITUACAO_USUARIO, CoreMatchers.is(SituacaoUsuario.ATIVO.name())),
							jsonPath(VAR_DATA_CRIACAO, CoreMatchers.notNullValue()),
							jsonPath(VAR_DATA_ATUALIZACAO, CoreMatchers.notNullValue()))
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
	 * {@link ClienteController#salvarCliente(ClienteDto, org.springframework.validation.Errors)}.
	 */
	@DisplayName("salvarCliente - Deve retornar Bad Request ao enviar CPF = NULL")
	@Order(2)
	@Test
	void deveNaoConterCpfAoSalvarCliente() {
		clienteDto.setCpf(null);

		ResultActions result;
		try {
			result = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(clienteDto)));

			result.andExpect(r -> assertEquals("POST", r.getRequest().getMethod())).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
	 * {@link ClienteController#deletarCliente(UUID)}.
	 */
	@DisplayName("deletarCliente - Deletar cliente com sucesso")
	@Order(3)
	@Test
	void deveDeletarClienteComSucesso() {

		BDDMockito.when(clienteService.findById(randomUuid)).thenReturn(Optional.of(cliente));
		
		ResultActions result;
		try {
			result = mockMvc.perform(delete("/clientes/".concat(randomUuid.toString())).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(clienteDto)));

			result.andExpect(r -> assertEquals("DELETE", r.getRequest().getMethod())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andDo(MockMvcResultHandlers.print());
		} catch (JsonProcessingException e) {
			log.error(e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}

	}
	
	

}
