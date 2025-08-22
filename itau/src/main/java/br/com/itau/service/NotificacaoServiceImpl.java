package br.com.itau.service;

import org.springframework.stereotype.Service;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.interfaces.NotificacaoService;
import br.com.itau.rabbit.NotificaoProducer;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl   implements NotificacaoService {

	  private final NotificaoProducer producer;
	  
	
	  public void enviarNotificacaoCadastroPendente(DadosPessoaisDTO dto) {
		   producer.enviarParaFilaComAtraso(dto);
	  }

}
