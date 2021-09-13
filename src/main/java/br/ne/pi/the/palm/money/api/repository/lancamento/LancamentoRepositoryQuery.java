package br.ne.pi.the.palm.money.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaCategoria;
import br.ne.pi.the.palm.money.api.dto.LancamentoEstatisticaDia;
import br.ne.pi.the.palm.money.api.model.Lancamento;
import br.ne.pi.the.palm.money.api.repository.filter.LancamentoFilter;
import br.ne.pi.the.palm.money.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {
	
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);

	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
