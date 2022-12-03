package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PARA ACESSAR O BANCO H2 APOS EXECUTAR A APLICACAO: http://localhost:8080/h2-console/
 * PARA ACESSAR A DOCUMENTACAO DO SWAGGER APOS EXECUTAR A APLICACAO: http://localhost:8080/v2/api-docs E COLAR O JSON EM https://editor.swagger.io/
 * http://localhost:8080/swagger-resources
 * http://localhost:8080/swagger-resources/configuration/ui
 * **/
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
