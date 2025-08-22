package br.com.itau.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import br.com.itau.model.DadosPessoaisModel;

public interface DadosPessoaisService {

	DadosPessoaisModel buscaCadastroPorId(Integer id) throws DadosPessoaisExcpetion;

	Page<DadosPessoaisDTO> buscaTodosCadastros(Pageable pagina);

	DadosPessoaisDTO atualizaDadosPessoais(Integer id, DadosPessoaisDTO dto) throws DadosPessoaisExcpetion;

	void deletarDadosPessoaisPorId(Integer id) throws DadosPessoaisExcpetion;

	DadosPessoaisDTO cadastraNovoDadoPessoais(DadosPessoaisDTO dadosPessoaisDTO) throws DadosPessoaisExcpetion;

	DadosPessoaisDTO patchDadosPessoais(Integer id, Map<String, Object> updates) throws DadosPessoaisExcpetion;

}
