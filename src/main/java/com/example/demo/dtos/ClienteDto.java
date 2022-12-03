package com.example.demo.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;

import lombok.AllArgsConstructor;
import lombok.Data ;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {

	@NotBlank
	private String nomeCompleto;

	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@NotNull
	@Email
	private String email;

	@NotNull(message = "CPF não pode ser nulo.")
	@CPF
	private String cpf;

	@NotBlank
	private String telefone;
}
