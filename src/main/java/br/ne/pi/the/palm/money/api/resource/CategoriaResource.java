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
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Categoria retorno = categoriaRepository.findOne(codigo);
		return retorno != null ? ResponseEntity.ok(retorno) : ResponseEntity.notFound().build();
		
	}
}
