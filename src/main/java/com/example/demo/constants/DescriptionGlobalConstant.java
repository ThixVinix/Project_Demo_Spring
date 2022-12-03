package com.example.demo.constants;

public class DescriptionGlobalConstant {

	private DescriptionGlobalConstant() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final String BAD_REQUEST_MESSAGE = "A requisicao que busca criar um cliente nao respeita o schema ou esta semanticamente errada.";
	public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Serviço nao esta disponivel no momento. Serviço solicitado pode estar em manutencao ou fora da janela de funcionamento.";
	
}
