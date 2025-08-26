package br.com.itau.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import br.com.itau.interfaces.DadosPessoaisService;
import br.com.itau.mapper.DadosPessoaisMapper;
import br.com.itau.model.DadosPessoaisModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/dados-pessoais")
@RequiredArgsConstructor
@Slf4j
public class DadosPessoaisController {
	
	private final DadosPessoaisService dadosPessoaisService;
	private final DadosPessoaisMapper mapper;
	
	@PostMapping
	ResponseEntity<DadosPessoaisResponseDTO> cadastraNovosDadosPessoais(@RequestBody DadosPessoaisDTO dadosPessoaisDTO) throws DadosPessoaisExcpetion{
		
		
		DadosPessoaisResponseDTO dp = dadosPessoaisService.cadastraNovoDadoPessoais(dadosPessoaisDTO);
		
		log.info("Cadastro efetuado com sucesso");
		
		return ResponseEntity.status(HttpStatus.CREATED).body(dp);
		
	}
	
	@GetMapping
	ResponseEntity<Page<DadosPessoaisResponseDTO>> consultaTodosCadastrosPessoais(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws DadosPessoaisExcpetion {

		Pageable pagina = PageRequest.of(page, size);

		return ResponseEntity.ok(dadosPessoaisService.buscaTodosCadastros(pagina));

	}
	
	@GetMapping("/{id}")
	ResponseEntity<DadosPessoaisResponseDTO> consultaCadastroPessoalPorId(@PathVariable Integer id)
			throws DadosPessoaisExcpetion {

		DadosPessoaisModel dadosPessoais = dadosPessoaisService.buscaCadastroPorId(id);
		log.info("Encontrados registros para a consulta de dados pessoais : "  + dadosPessoais);

		return ResponseEntity.ok(mapper.modelParaDTO(dadosPessoais));

	}
	
	
	@PutMapping("/{id}")
	ResponseEntity<DadosPessoaisResponseDTO> atualizaDadosPessoais( @PathVariable Integer id, @RequestBody DadosPessoaisDTO dto) throws DadosPessoaisExcpetion{
		
		
		return ResponseEntity.ok(dadosPessoaisService.atualizaDadosPessoais(id,dto));
		
	}
		
	@DeleteMapping("/{id}")
	ResponseEntity<Void> deletaDadosPessoais(@PathVariable Integer id) throws DadosPessoaisExcpetion{
		
		dadosPessoaisService.deletarDadosPessoaisPorId(id);
		log.info("Informação deletada com sucesso");

		
		return ResponseEntity.noContent().build();		
	}

	
	@PatchMapping("/{id}")
	ResponseEntity<DadosPessoaisResponseDTO> patchDados(@PathVariable Integer id, @RequestBody Map<String, Object> updates)
			throws DadosPessoaisExcpetion {
		return ResponseEntity.ok(dadosPessoaisService.patchDadosPessoais(id, updates));
	}

}
