package br.ne.pi.the.palm.money.api.service;

import java.util.Optional;

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
	
	public Pessoa salvar(Pessoa pessoa) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);
	}
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa retorno = buscarPessoaPeloCodigo(codigo);

		retorno.getContatos().clear();
		retorno.getContatos().addAll(pessoa.getContatos());
		retorno.getContatos().forEach(c -> c.setPessoa(retorno));
		
		BeanUtils.copyProperties(pessoa, retorno, "codigo", "contatos");
		return pessoaRepository.save(retorno);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa retorno = buscarPessoaPeloCodigo(codigo);
		retorno.setAtivo(ativo);
		pessoaRepository.save(retorno);
	}
	
	public Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Optional<Pessoa> retorno = pessoaRepository.findById(codigo);
		if(!retorno.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return retorno.get();
	}
}
