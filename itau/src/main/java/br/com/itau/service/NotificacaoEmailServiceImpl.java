package br.com.itau.service;

import org.springframework.stereotype.Service;

import br.com.itau.dto.DadosPessoaisDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoEmailServiceImpl implements NotificacaoEmailService {
	

		public void enviarEmailCadastroPendente(DadosPessoaisDTO dto) {
			try {
				
				String body = "Olá, " + dto.nome()
						+ ",\n\nSeu cadastro está pendente de aprovação. Assim que for analisado, você será notificado.\n\n"
						+ "Resumo: " + dto.nome() + " " + dto.sobreNome() + ", " + dto.idade() + " anos, " + dto.pais();
				
				System.out.println("E-mail enviado com sucesso,\n" + body);
				log.info("Email de pendencia recebido com sucesso");
				
			} catch (Exception e) {
				log.error("Fala ao enviar o e-mail de cadastro pendente");
				throw new RuntimeException("Falha ao enviar e-mail", e);
			}
		}
	}
