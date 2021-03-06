package br.ne.pi.the.palm.money.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaPessoa;
import br.ne.pi.the.palm.money.api.mail.Mailer;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.model.Pessoa;
import br.ne.pi.the.palm.money.api.model.Usuario;
import br.ne.pi.the.palm.money.api.repository.LancamentoRepository;
import br.ne.pi.the.palm.money.api.repository.PessoaRepository;
import br.ne.pi.the.palm.money.api.repository.UsuarioRepository;
import br.ne.pi.the.palm.money.api.service.exception.PessoaInexistenteOuInativaException;
import br.ne.pi.the.palm.money.api.storage.S3;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	@Autowired
	private S3 s3;

	@Scheduled(cron = "0 0 6 * * *") // segundo, minuto, hora, dia do mês, mês, dia da semana
	public void avisarSobreLancamentosVencidos() {
		if (logger.isDebugEnabled()) {
			logger.debug("Preparando envio de e-mails de aviso de lançamentos vencidos.");
		}
		
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if (vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso.");
			return;
		}
	
		logger.info("Exitem {} lançamentos vencidos.", vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);
		
		if (destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas o sistema não encontrou destinatários.");
			return;
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);

		logger.info("Envio de e-mail de aviso concluído."); 
	}
	
	/*@Scheduled(fixedDelay = 1000 * 5)
	public void avisarSobreLancamentosVencidos() {
		System.out.println(">>>>>>>>>>>>>>> Método sendo executado...");
	}*/
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
				new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	public Lancamento salvar(Lancamento lancamento) {
		validarPessoa(lancamento);
		
		if (StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento retorno = buscarLancamentoExistente(codigo);
		
		if(!lancamento.getPessoa().equals(retorno.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		if (StringUtils.isEmpty(lancamento.getAnexo()) && StringUtils.hasText(retorno.getAnexo())) {
			s3.remover(retorno.getAnexo());
		} else if (StringUtils.hasText(lancamento.getAnexo()) && !lancamento.getAnexo().equals(retorno.getAnexo())) {
			s3.substituir(retorno.getAnexo(), lancamento.getAnexo());
		}
		
		BeanUtils.copyProperties(lancamento, retorno, "codigo");
		return lancamentoRepository.save(retorno);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if(lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.getOne(lancamento.getPessoa().getCodigo());
		}
		if(pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Optional<Lancamento> retorno = lancamentoRepository.findById(codigo);
		if(!retorno.isPresent()) {
			throw new IllegalArgumentException();
		}
		return retorno.get();
	}
}
