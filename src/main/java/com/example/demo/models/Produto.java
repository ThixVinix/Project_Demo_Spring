package com.example.demo.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Modelo do Produto", description = "Modelo para registro das informações do produto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_PRODUTOS")
public class Produto implements Serializable {

	private static final long serialVersionUID = 5613201841816984841L;

	@ApiModelProperty(value = "UUID do produto", name = "Identificador único universal (universally unique identifier - UUID) do cliente", example = "f3d941e5-75ec-486a-88d0-a9f2092b7883")
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "produto_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID produtoId;

	@ApiModelProperty(value = "Nome do produto", name = "nome", example = "Notebook X")
	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@ApiModelProperty(value = "Descrição do produto", name = "descricao", example = "Modelo Y, Geração Z...")
	@Column(name = "descricao", nullable = false, length = 250)
	private String descricao;

	@ApiModelProperty(value = "Link da imagem do produto", name = "imagemProduto")
	@Column(name = "img_produto")
	private String imagemProduto;

	@ApiModelProperty(value = "Valor do produto", name = "valor", example = "2000.00")
	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@ApiModelProperty(value = "Data de criação dos dados do Cliente", name = "dataCriacaoRegistro", example = "2002-06-12'T'09:40:32'Z'", required = true, reference = "yyyy-MM-dd'T'HH:mm:ss'Z'", hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", locale = "pt-BR", timezone = "America/Sao_Paulo")
	@Column(nullable = false)
	private LocalDateTime dataCriacao;

	@ApiModelProperty(value = "Data de atualização dos dados do Cliente", name = "dataAtualizacaoRegistro", example = "2002-06-12'T'09:40:32'Z'", reference = "yyyy-MM-dd'T'HH:mm:ss'Z'", hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", locale = "pt-BR", timezone = "America/Sao_Paulo")
	@Column
	private LocalDateTime dataAtualizacao;

}
