package com.example.demo.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PedidoDto {

	@NotBlank
	private String codNotaFiscal;
	
}
