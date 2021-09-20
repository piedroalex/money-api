package br.ne.pi.the.palm.money.api.resource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.ne.pi.the.palm.money.api.dto.Anexo;
import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaCategoria;
import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaDia;
import br.ne.pi.the.palm.money.api.event.RecursoCriadoEvent;
import br.ne.pi.the.palm.money.api.exceptionhandler.MoneyExceptionHandler.Erro;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.repository.LancamentoRepository;
import br.ne.pi.the.palm.money.api.repository.filter.LancamentoFilter;
import br.ne.pi.the.palm.money.api.repository.projection.ResumoLancamento;
import br.ne.pi.the.palm.money.api.service.LancamentoService;
import br.ne.pi.the.palm.money.api.service.exception.PessoaInexistenteOuInativaException;
import br.ne.pi.the.palm.money.api.storage.S3;

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
	private LancamentoService lancamentoService;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource;	

	@Autowired
	private S3 s3;
	
	@PostMapping("/anexo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public Anexo uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
		String nome = s3.salvarTemporariamente(anexo);
		return new Anexo(nome, s3.configurarUrl(nome));
	}
	
	@GetMapping("/relatorios/por-pessoa")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<byte[]> relatorioPorPessoa(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio, 
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws Exception {
		byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@GetMapping("/estatisticas/por-dia")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaDia> porDia() {
		return this.lancamentoRepository.porDia(LocalDate.now());
	}
	
	@GetMapping("/estatisticas/por-categoria")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaCategoria> porCategoria() {
		return this.lancamentoRepository.porCategoria(LocalDate.now());
	}
	
	/**
	 * Retorna uma lista com todas os lancamentos.
	 * 
	 * @param LancamentoFilter lancamentoFilter, Pageable pageable
	 * @return Page<Lancamento>
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	/**
	 * Retorna uma lista com todas os lancamentos.
	 * 
	 * @param LancamentoFilter lancamentoFilter, Pageable pageable
	 * @return Page<Lancamento>
	 */
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}
	
	/**
	 * Retorna um lançamento de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Lancamento>
	 */
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Lancamento> retorno = lancamentoRepository.findById(codigo);
		return retorno.isPresent() ? ResponseEntity.ok(retorno.get()) : ResponseEntity.notFound().build();
		
	}
	
	/**
	 * Salva um novo lançamento.
	 * 
	 * @param Lancamento lancamento, HttpServletResponse response
	 * @return ResponseEntity<Lancamento>
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento retorno = lancamentoService.salvar(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, retorno.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
	}
	
	/**
	 * Remove um lançamento de acordo com um código.
	 * 
	 * @param Long codigo
	 * @return ResponseEntity<Lancamento>
	 */
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long codigo) {
		lancamentoRepository.deleteById(codigo);
	}
	
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento){
		try {
			Lancamento retorno = lancamentoService.atualizar(codigo, lancamento);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}
}
