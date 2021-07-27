package br.ne.pi.the.palm.money.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.ne.pi.the.palm.money.api.event.RecursoCriadoEvent;

/** 
 * Classe que representa um listener que ouve o evento disparado pela aplicação.
 * 
 * @author Pedro Alex
 * */
@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	/**
	 * Método que adiciona o header Location quando um evento for disparado.
	 * 
	 * @param RecursoCriadoEvent event
	 */
	@Override
	public void onApplicationEvent(RecursoCriadoEvent event) {
		HttpServletResponse response = event.getResponse();
		Long codigo = event.getCodigo();
		this.adicionarHeaderLocation(response, codigo);
	}
	
	/**
	 * Método que adiciona o header Location ao cabeçalho da resposta.
	 * 
	 * @param HttpServletResponse response, Long codigo
	 */
	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}").buildAndExpand(codigo).toUri();
		response.setHeader("Location", uri.toASCIIString());
	}
}
