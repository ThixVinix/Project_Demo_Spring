package com.example.demo.dtos;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ProdutoDto {

	@NotBlank
	private String nome;
	
	@NotBlank
	private String descricao;
	
	private String imagemProduto;
	
	@NotNull
	@Min(value = 0)
	private BigDecimal valor;
	
}
