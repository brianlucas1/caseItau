package br.com.itau.service;

import br.com.itau.dto.DadosPessoaisDTO;

public interface NotificacaoEmailService {

	void enviarEmailCadastroPendente(DadosPessoaisDTO dto);

}
