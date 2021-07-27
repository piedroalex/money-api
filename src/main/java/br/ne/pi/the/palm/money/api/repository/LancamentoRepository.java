package br.ne.pi.the.palm.money.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.repository.lancamento.LancamentoRepositoryQuery;

/** 
 * Classe que acessa os dados referentes à Lancamento.
 * 
 * @author Pedro Alex
 * */
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{

}
