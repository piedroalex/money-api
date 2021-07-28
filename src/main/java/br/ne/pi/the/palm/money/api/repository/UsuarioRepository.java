package br.ne.pi.the.palm.money.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ne.pi.the.palm.money.api.model.Usuario;

/** 
 * Classe que acessa os dados referentes à Usuario.
 * 
 * @author Pedro Alex
 * */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	public Optional<Usuario> findByEmail(String email);
	
}