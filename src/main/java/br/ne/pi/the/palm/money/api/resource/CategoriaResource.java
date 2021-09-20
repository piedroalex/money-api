package br.ne.pi.the.palm.money.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ne.pi.the.palm.money.api.event.RecursoCriadoEvent;
import br.ne.pi.the.palm.money.api.model.Categoria;
import br.ne.pi.the.palm.money.api.repository.CategoriaRepository;

/** 
 * Classe de acesso ao recurso Categoria.
 * 
 * @author Pedro Alex
 * */
@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	/**
	 * Retorna uma lista com todas as categorias.
	 * 
	 * @return List<Categoria>
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public List<Categoria> listar(){
		return categoriaRepository.findAll();
	}
	
	/**
	 * Salva uma nova categoria.
	 * 
	 * @param Categoria categoria, HttpServletResponse response
	 * @return ResponseEntity<Categoria>
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria retorno = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, retorno.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
	}
	
	/**
	 * Retorna uma categoria de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Categoria>
	 */
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> retorno = categoriaRepository.findById(codigo);
		return retorno.isPresent() ? ResponseEntity.ok(retorno.get()) : ResponseEntity.notFound().build();
		
	}
}
