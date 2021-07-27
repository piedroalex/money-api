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

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Categoria> listar(){
		return categoriaRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria retorno = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, retorno.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Categoria retorno = categoriaRepository.findOne(codigo);
		return retorno != null ? ResponseEntity.ok(retorno) : ResponseEntity.notFound().build();
		
	}
}
