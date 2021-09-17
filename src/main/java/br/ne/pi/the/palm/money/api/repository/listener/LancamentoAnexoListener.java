package br.ne.pi.the.palm.money.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.ne.pi.the.palm.money.api.MoneyApiApplication;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.storage.S3;

public class LancamentoAnexoListener {

	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if (StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = MoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}
	
}
