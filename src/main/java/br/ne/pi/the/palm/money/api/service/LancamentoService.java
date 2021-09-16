package br.ne.pi.the.palm.money.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaPessoa;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.model.Pessoa;
import br.ne.pi.the.palm.money.api.repository.LancamentoRepository;
import br.ne.pi.the.palm.money.api.repository.PessoaRepository;
import br.ne.pi.the.palm.money.api.service.exception.PessoaInexistenteOuInativaException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Scheduled(cron = "0 0 6 * * *") // segundo, minuto, hora, dia do mês, mês, dia da semana
	public void avisarSobreLancamentosVencidos() {
		System.out.println(">>>>>>>>>>>>>>> Método sendo executado...");
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
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		if(pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento retorno = buscarLancamentoExistente(codigo);
		if(!lancamento.getPessoa().equals(retorno.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, retorno, "codigo");
		return lancamentoRepository.save(retorno);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if(lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		}
		if(pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento retorno = lancamentoRepository.findOne(codigo);
		if(retorno == null) {
			throw new IllegalArgumentException();
		}
		return retorno;
	}
}
