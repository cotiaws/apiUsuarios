package br.com.cotiinformatica.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cotiinformatica.components.JwtTokenComponent;
import br.com.cotiinformatica.components.SHA256Component;
import br.com.cotiinformatica.dtos.AutenticarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.AutenticarUsuarioResponseDto;
import br.com.cotiinformatica.dtos.CriarUsuarioRequestDto;
import br.com.cotiinformatica.dtos.CriarUsuarioResponseDto;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.repositories.PerfilRepository;
import br.com.cotiinformatica.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	//Injeção de dependência
	@Autowired UsuarioRepository usuarioRepository;
	@Autowired PerfilRepository perfilRepository;
	@Autowired SHA256Component sha256Component;
	@Autowired JwtTokenComponent jwtTokenComponent;
	
	/*
	 * Método para realizar o cadastro dos usuários no sistema
	 */
	public CriarUsuarioResponseDto criarUsuario(CriarUsuarioRequestDto request) {

		//Verificar se o email já está cadastrado no banco de dados
		if (usuarioRepository.findByEmail(request.getEmail()) != null) {
			throw new IllegalArgumentException("O email informado já está cadastrado, tente outro.");
		}
		
		//capturar os dados do usuário
		var usuario = new Usuario();
		
		usuario.setId(UUID.randomUUID());
		usuario.setNome(request.getNome());
		usuario.setEmail(request.getEmail());
		usuario.setSenha(sha256Component.encrypt(request.getSenha()));
		usuario.setPerfil(perfilRepository.findByNome("Operador"));
		
		//gravar o usuário no banco de dados
		usuarioRepository.save(usuario);
		
		//retornar os dados do usuário
		var response = new CriarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataCriacao(Instant.now());
		response.setPerfil(usuario.getPerfil().getNome());
		
		return response;
	}
	
	/*
	 * Método para realizar a autenticação dos usuários no sistema
	 */
	public AutenticarUsuarioResponseDto autenticarUsuario(AutenticarUsuarioRequestDto request) {

		//Consultar o usuário no banco de dados através do email e da senha
		var usuario = usuarioRepository.findByEmailAndSenha(request.getEmail(), sha256Component.encrypt(request.getSenha()));
		
		//verificar se o usuário não foi encontrado
		if(usuario == null) {
			throw new IllegalArgumentException("Acesso negado. Usuário não encontrado.");
		}
		
		//retornar os dados do usuário
		var response = new AutenticarUsuarioResponseDto();
		
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil().getNome());
		response.setToken(jwtTokenComponent.getToken(usuario));
		
		return response;
	}
}



