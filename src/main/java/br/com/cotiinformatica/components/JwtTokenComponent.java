package br.com.cotiinformatica.components;

import java.util.Date;

import org.springframework.stereotype.Component;

import br.com.cotiinformatica.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenComponent {

	/*
	 * Método para criar e retornar um TOKEN JWT 
	 * para o usuário autenticado no sistema
	 */
	public String getToken(Usuario usuario) {
		
		//chave para assinatura dos tokens
		var secretKey = "9658011f-94ec-49e6-8602-f517a94fc6bb";
		
		return Jwts.builder()
                .setSubject(usuario.getEmail()) //email do usuário
                .claim("perfil", usuario.getPerfil().getNome()) //perfil do usuário
                .setIssuedAt(new Date())  //data de geração do token
                .setExpiration(new Date(System.currentTimeMillis() + 1800000)) //data de expiração do token (30min)
                .signWith(SignatureAlgorithm.HS256, secretKey) //chave para assinatura do token
                .compact();

	}
}
