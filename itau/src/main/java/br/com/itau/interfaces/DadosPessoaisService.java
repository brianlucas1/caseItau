package br.com.itau.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import br.com.itau.model.DadosPessoaisModel;

public interface DadosPessoaisService {

	DadosPessoaisModel buscaCadastroPorId(Integer id) throws DadosPessoaisExcpetion;

	Page<DadosPessoaisResponseDTO> buscaTodosCadastros(Pageable pagina);

	DadosPessoaisResponseDTO atualizaDadosPessoais(Integer id, DadosPessoaisDTO dto) throws DadosPessoaisExcpetion;

	void deletarDadosPessoaisPorId(Integer id) throws DadosPessoaisExcpetion;

	DadosPessoaisResponseDTO cadastraNovoDadoPessoais(DadosPessoaisDTO dadosPessoaisDTO) throws DadosPessoaisExcpetion;

	DadosPessoaisResponseDTO patchDadosPessoais(Integer id, Map<String, Object> updates) throws DadosPessoaisExcpetion;

}
