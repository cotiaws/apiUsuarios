package br.com.cotiinformatica.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.cotiinformatica.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

	/*
	 * Consulta para retornar 1 usuário do banco de dados
	 * através do email
	 */
	@Query("""
			select u from Usuario u 
			where u.email = :param1
			""")
	Usuario findByEmail(
			@Param("param1") String email
			);
	
	/*
	 * Consulta para retornar 1 usuário do banco de dados
	 * através do email e da senha
	 */
	@Query("""
			select u from Usuario u
			join u.perfil p 
			where u.email = :param1
			  and u.senha = :param2
			""")
	Usuario findByEmailAndSenha(
			@Param("param1") String email,
			@Param("param2") String senha
			);
}



