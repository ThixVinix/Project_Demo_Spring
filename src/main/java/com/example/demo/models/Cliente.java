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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.demo.enums.SituacaoUsuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ApiModel(value = "Modelo do Cliente", description = "Modelo para registro das informações do cliente")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_CLIENTES")
public class Cliente implements Serializable {

	private static final long serialVersionUID = -3723852864967132330L;

	@ApiModelProperty(value = "UUID do cliente", name = "Identificador único universal (universally unique identifier - UUID) do cliente", example = "bcd9a97c-21fa-45d5-8647-7260b0fed481")
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "cliente_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID clienteId;

	@ApiModelProperty(value = "Nome completo do Cliente", name = "nomeCompleto", example = "Fulano Beltrano", required = true)
	@Column(name = "nome_completo", nullable = false)
	private String nomeCompleto;

	@ApiModelProperty(value = "Apelido do Cliente", name = "username", example = "Fulano223", required = true)
	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@ApiModelProperty(value = "Senha do Cliente", name = "password", example = "&Ks93p^Ga15@", required = true)
	@ToString.Exclude
	@JsonIgnore
	@Column(nullable = false)
	private String password;

	@ApiModelProperty(value = "E-mail do Cliente", name = "email", example = "fulano@test.com", required = true)
	@Column(name = "email", unique = true, length = 50, nullable = false)
	private String email;

	@ApiModelProperty(value = "CPF do Cliente", name = "cpf", example = "92494866006", required = true)
	@Column(name = "cpf", unique = true, nullable = false)
	private String cpf;

	@ApiModelProperty(value = "Telefone do Cliente", name = "telefone", example = "11777497008", required = true)
	@Column(nullable = false, length = 20)
	private String telefone;

	@ApiModelProperty(value = "Situação do Cliente no sistema", name = "situacaoUsuario", example = "ATIVO")
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SituacaoUsuario situacaoUsuario;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private Set<Pedido> pedidos;

	@ApiModelProperty(value = "Data de criação dos dados do Cliente", name = "dataCriacaoRegistro", example = "2002-06-12'T'09:40:32'Z'", required = true, reference = "yyyy-MM-dd'T'HH:mm:ss'Z'", hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", locale = "pt-BR", timezone = "America/Sao_Paulo")
	@Column(nullable = false)
	private LocalDateTime dataCriacaoRegistro;

	@ApiModelProperty(value = "Data de atualização dos dados do Cliente", name = "dataAtualizacaoRegistro", example = "2002-06-12'T'09:40:32'Z'", reference = "yyyy-MM-dd'T'HH:mm:ss'Z'", hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", locale = "pt-BR", timezone = "America/Sao_Paulo")
	@Column
	private LocalDateTime dataAtualizacaoRegistro;

}
