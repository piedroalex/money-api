package br.ne.pi.the.palm.money.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.ne.pi.the.palm.money.api.model.Pessoa;
import br.ne.pi.the.palm.money.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa retorno = buscarPessoaPeloCodigo(codigo);
		
		BeanUtils.copyProperties(pessoa, retorno, "codigo");
		return pessoaRepository.save(retorno);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa retorno = buscarPessoaPeloCodigo(codigo);
		retorno.setAtivo(ativo);
		pessoaRepository.save(retorno);
	}
	
	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa retorno = pessoaRepository.findOne(codigo);
		if(retorno == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return retorno;
	}
}
