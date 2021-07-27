package br.ne.pi.the.palm.money.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ne.pi.the.palm.money.api.event.RecursoCriadoEvent;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.repository.LancamentoRepository;

/** 
 * Classe de acesso ao recurso Lancamento.
 * 
 * @author Pedro Alex
 * */
@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
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
	
	/**
	 * Salva um novo lançamento.
	 * 
	 * @param Lancamento lancamento, HttpServletResponse response
	 * @return ResponseEntity<Lancamento>
	 */
	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento retorno = lancamentoRepository.save(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, retorno.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
	}
}
