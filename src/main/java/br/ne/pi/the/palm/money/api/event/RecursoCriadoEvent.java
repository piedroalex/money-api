package br.ne.pi.the.palm.money.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

/** 
 * Classe que representa um evento disparado pela aplicação.
 * 
 * @author Pedro Alex
 * */
public class RecursoCriadoEvent extends ApplicationEvent{

	private static final long serialVersionUID = 1L;

	private HttpServletResponse response;
	private Long codigo;
	
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
		this.response = response;
		this.codigo = codigo;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

}
