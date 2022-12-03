package com.example.demo.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.demo.enums.SituacaoPedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Modelo do pedido", description = "Modelo para registro das informações do pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_PEDIDOS")
public class Pedido implements Serializable {

	private static final long serialVersionUID = 2382882198478107014L;

	@ApiModelProperty(value = "UUID do pedido", name = "Identificador único universal (universally unique identifier - UUID) do pedido", example = "12e28151-5faa-4714-a36a-fb03ffc9cca0")
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "pedido_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID pedidoId;

	@ApiModelProperty(value = "Código da nota fiscal do produto", name = "codNotaFiscal")
	@Column(name = "cod_nota_fiscal", unique = true)
	private String codNotaFiscal;

	@ApiModelProperty(value = "Situação do Produto no sistema", name = "situacaoPedido", example = "SOLICITADO")
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SituacaoPedido situacaoPedido;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<Produto> produtos;

	@ApiModelProperty(value = "Data de criação dos dados do Pedido", name = "dataCriacao", example = "2002-06-12'T'09:40:32'Z'", required = true, reference = "yyyy-MM-dd'T'HH:mm:ss'Z'", hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", locale = "pt-BR", timezone = "America/Sao_Paulo")
	@Column(nullable = false)
	private LocalDateTime dataCriacao;

}
