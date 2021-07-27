package br.ne.pi.the.palm.money.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ne.pi.the.palm.money.api.model.Pessoa;

/** 
 * Classe que acessa os dados referentes à Pessoa.
 * 
 * @author Pedro Alex
 * */
public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

}
