package br.ne.pi.the.palm.money.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.repository.LancamentoRepository;

/** 
 * Classe de acesso ao recurso Pessoa.
 * 
 * @author Pedro Alex
 * */
@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	/**
	 * Retorna uma lista com todas os lancamentos.
	 * 
	 * @return List<Lancamento>
	 */
	@GetMapping
	public List<Lancamento> listar(){
		return lancamentoRepository.findAll();
	}
	
	/**
	 * Retorna um lançamento de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Lancamento>
	 */
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		Lancamento retorno = lancamentoRepository.findOne(codigo);
		return retorno != null ? ResponseEntity.ok(retorno) : ResponseEntity.notFound().build();
		
	}
	
}
