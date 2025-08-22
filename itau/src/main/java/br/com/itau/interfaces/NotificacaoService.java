package br.com.itau.interfaces;

import br.com.itau.dto.DadosPessoaisDTO;

public interface NotificacaoService {


	void enviarNotificacaoCadastroPendente(DadosPessoaisDTO dto);

}
