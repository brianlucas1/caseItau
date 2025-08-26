package br.com.itau.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import br.com.itau.interfaces.DadosPessoaisService;
import br.com.itau.interfaces.NotificacaoService;
import br.com.itau.mapper.DadosPessoaisMapper;
import br.com.itau.model.DadosPessoaisModel;
import br.com.itau.repository.DadosPessoaisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class DadosPessoaisServiceImpl  implements DadosPessoaisService{
	
	private final DadosPessoaisRepository dadosPessoaisRepo;
	private final DadosPessoaisMapper mapper;
	private final NotificacaoService notiService;
	
	
	@Override
	public DadosPessoaisResponseDTO cadastraNovoDadoPessoais(DadosPessoaisDTO dto) throws DadosPessoaisExcpetion {

		if (dadosPessoaisRepo.existsByEmail(dto.email())) {
			log.error("Erro na tentativa de um novo cadastro : Email já cadastrado");
			throw new DadosPessoaisExcpetion("Email já cadastrado.");
		}

		DadosPessoaisModel dp = mapper.dtoParaModel(dto);

		DadosPessoaisModel dpSalvo = dadosPessoaisRepo.save(dp);

		notiService.enviarNotificacaoCadastroPendente(dto);

		return mapper.modelParaDTO(dpSalvo);
	}


	@Override
	public DadosPessoaisModel buscaCadastroPorId(Integer id) throws DadosPessoaisExcpetion {
		
		return dadosPessoaisRepo.findById(id)
				.orElseThrow(() -> new DadosPessoaisExcpetion("Não foi encontrado dados para o ID informado, }" + id));

	}

	@Override
	public Page<DadosPessoaisResponseDTO> buscaTodosCadastros(Pageable pagina) {

		Page<DadosPessoaisModel> dpPaginado = dadosPessoaisRepo.findAll(pagina);
		log.info("Encontrados registros para a consulta de dados pessoais : "  + pagina.getPageSize());

		return dpPaginado.map(mapper::modelParaDTO);
	}

	@Override
	@Transactional
	public DadosPessoaisResponseDTO atualizaDadosPessoais(Integer id, DadosPessoaisDTO dto) throws DadosPessoaisExcpetion {

		DadosPessoaisModel dpModel = buscaCadastroPorId(id);

		mapper.atualizaModelPeloDTO(dto, dpModel);

		return mapper.modelParaDTO(dadosPessoaisRepo.save(dpModel));
	}

	@Override
	public void deletarDadosPessoaisPorId(Integer id) throws DadosPessoaisExcpetion {
		
		  if (dadosPessoaisRepo.existsById(id)) {
			  dadosPessoaisRepo.deleteById(id);
		    }else {
		    	throw new DadosPessoaisExcpetion("Não foi excluir o registro para o ID informado, " + id);
		    }

	}
	
	@Override
	@Transactional
	public DadosPessoaisResponseDTO patchDadosPessoais(Integer id, Map<String, Object> updates) throws DadosPessoaisExcpetion {
		if (updates == null || updates.isEmpty()) {
			log.info("Nenhum campo para atualizar");
			throw new DadosPessoaisExcpetion("Nenhum campo para atualizar.");
		}
		if (updates.size() > 1) {
			throw new DadosPessoaisExcpetion("PATCH deve atualizar apenas um campo por requisição.");
		}
		DadosPessoaisModel m = buscaCadastroPorId(id);
		String field = updates.keySet().iterator().next();
		Object value = updates.get(field);
		switch (field) {
		case "nome" -> m.setNome((String) value);
		case "sobrenome" -> m.setSobreNome((String) value);
		case "idade" -> m.setIdade((Integer) value);
		case "pais" -> m.setPais((String) value);
		case "email" -> {
			String novoEmail = (String) value;
			if (!novoEmail.equals(m.getEmail()) && dadosPessoaisRepo.existsByEmail(novoEmail)) {
				log.error("Email já cadastro");
				throw new DadosPessoaisExcpetion("Email já cadastrado.");
			}
			m.setEmail(novoEmail);
		}
		default -> throw new DadosPessoaisExcpetion("Campo não suportado para PATCH: " + field);
		}
		log.info("Encontrados registro atualizado com sucesso ");

		return mapper.modelParaDTO(dadosPessoaisRepo.save(m));
	}

}
