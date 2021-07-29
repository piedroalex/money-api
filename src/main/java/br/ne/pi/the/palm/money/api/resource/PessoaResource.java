package br.ne.pi.the.palm.money.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.ne.pi.the.palm.money.api.event.RecursoCriadoEvent;
import br.ne.pi.the.palm.money.api.model.Pessoa;
import br.ne.pi.the.palm.money.api.repository.PessoaRepository;
import br.ne.pi.the.palm.money.api.repository.filter.PessoaFilter;
import br.ne.pi.the.palm.money.api.service.PessoaService;

/** 
 * Classe de acesso ao recurso Pessoa.
 * 
 * @author Pedro Alex
 * */
@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	/**
	 * Retorna uma lista com todas as pessoas de acordo com os filtros.
	 * 
	 * @param PessoaFilter pessoaFilter, Pageable pageable
	 * @return Page<Pessoa>
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public Page<Pessoa> pesquisar(PessoaFilter pessoaFilter, Pageable pageable){
		return pessoaRepository.filtrar(pessoaFilter, pageable);
	}
	
	/**
	 * Salva uma nova pessoa.
	 * 
	 * @param Pessoa pessoa, HttpServletResponse response
	 * @return ResponseEntity<Pessoa>
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa retorno = pessoaRepository.save(pessoa);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, retorno.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
	}
	
	/**
	 * Retorna uma pessoa de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Pessoa>
	 */
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
		Pessoa retorno = pessoaRepository.findOne(codigo);
		return retorno != null ? ResponseEntity.ok(retorno) : ResponseEntity.notFound().build();
		
	}
	
	/**
	 * Remove uma pessoa de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Pessoa>
	 */
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long codigo) {
		pessoaRepository.delete(codigo);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa){
		Pessoa retorno = pessoaService.atualizar(codigo, pessoa);
		return ResponseEntity.ok(retorno);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo){
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}
}
