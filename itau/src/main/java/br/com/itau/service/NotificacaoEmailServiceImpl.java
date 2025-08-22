package br.com.itau.service;

import org.springframework.stereotype.Service;

import br.com.itau.dto.DadosPessoaisDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacaoEmailServiceImpl implements NotificacaoEmailService {
	

		public void enviarEmailCadastroPendente(DadosPessoaisDTO dto) {
			try {
				
				String body = "Olá, " + dto.nome()
						+ ",\n\nSeu cadastro está pendente de aprovação. Assim que for analisado, você será notificado.\n\n"
						+ "Resumo: " + dto.nome() + " " + dto.sobreNome() + ", " + dto.idade() + " anos, " + dto.pais();
				
				System.out.println("E-mail enviado com sucesso,\n" + body);
				
			} catch (Exception e) {
				throw new RuntimeException("Falha ao enviar e-mail", e);
			}
		}
	}
