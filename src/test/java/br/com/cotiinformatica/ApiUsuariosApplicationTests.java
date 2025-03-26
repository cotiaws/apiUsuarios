package br.com.cotiinformatica;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiUsuariosApplicationTests {
	
	@Autowired MockMvc mockMvc; //fazer as chamadas para a API
	@Autowired ObjectMapper objectMapper; //converter objetos para JSON e vice-versa

	private static String email; //armazenar o email do usuário cadastrado
	private static String senha; //aramzenar a senha do usuário cadastrado
	
	@Test
	@Order(1)
	void criarUsuarioComSucesso() throws Exception {
		
		var faker = new Faker();
		
		var request = new CriarUsuarioRequestDto();
		request.setNome(faker.name().fullName()); //nome aleatório
		request.setEmail(faker.internet().emailAddress()); //email aleatório
		request.setSenha("@Admin123"); //senha fixa
		
		//enviando a requisição para cadastrar o usuário na API
		mockMvc.perform(post("/api/usuario/criar")
			   .contentType("application/json")
			   .content(objectMapper.writeValueAsString(request)))
		       .andExpect(status().isOk());
		
		//armazenando o email e senha do usuário cadastrado
		email = request.getEmail();
		senha = request.getSenha();
	}

	@Test
	@Order(2)
	void autenticarUsuarioComSucesso() throws Exception {
		
		//criando um objeto para enviar a requisição de autenticação
		var request = new AutenticarUsuarioRequestDto();
		request.setEmail(email); //preenchendo o email do usuário
		request.setSenha(senha); //preenchendo a senha do usuário
		
		//fazendo a requisição para o endpoint de autenticação da API
		mockMvc.perform(post("/api/usuario/autenticar")
			   .contentType("application/json")
			   .content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk());
	}

	@Test
	@Order(3)
	void erroAoCriarUsuarioJaExistente() throws Exception {
		
		var request = new CriarUsuarioRequestDto();
		request.setNome("Usuário teste"); //nome fixo
		request.setEmail(email); //email já cadastrado anteriormente
		request.setSenha("@Admin123"); //senha fixa
		
		//enviando a requisição para cadastrar o usuário na API
		var result = mockMvc.perform(post("/api/usuario/criar")
			   .contentType("application/json")
			   .content(objectMapper.writeValueAsString(request)))
		       .andExpect(status().isBadRequest())
		       .andReturn();
		
		//verificando a mensagem retornada pela API
		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("O email informado já está cadastrado, tente outro."));
	}

	@Test
	@Order(4)
	void acessoNegadoParaUsuarioInvalido() throws Exception {

		var request = new AutenticarUsuarioRequestDto();
		request.setEmail(email); //email já cadastrado anteriormente
		request.setSenha("@Teste2025"); //senha errada
		
		//enviando a requisição para cadastrar o usuário na API
		var result = mockMvc.perform(post("/api/usuario/autenticar")
			   .contentType("application/json")
			   .content(objectMapper.writeValueAsString(request)))
		       .andExpect(status().isBadRequest())
		       .andReturn();
		
		//verificando a mensagem retornada pela API
		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Acesso negado. Usuário não encontrado."));
	}
}



