package br.ne.pi.the.palm.money.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ne.pi.the.palm.money.api.model.Cidade;

public interface CidadeRepository  extends JpaRepository<Cidade, Long>{

	List<Cidade> findByEstadoCodigo(Long estadoCodigo);
	
}
