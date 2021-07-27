package br.ne.pi.the.palm.money.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ne.pi.the.palm.money.api.model.Categoria;

/** 
 * Classe que acessa os dados referentes à Categoria.
 * 
 * @author Pedro Alex
 * */
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
