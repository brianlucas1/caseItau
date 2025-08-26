package br.com.itau.mapper;


import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.model.DadosPessoaisModel;

@Mapper(componentModel = "spring")
public interface DadosPessoaisMapper {

	  @lombok.Generated
	DadosPessoaisResponseDTO modelParaDTO(DadosPessoaisModel pessoa);

	  @lombok.Generated
	  @Mapping(target = "id", ignore = true)
	  @Mapping(target = "status", ignore = true) // mantém PENDENTE no entity
	  DadosPessoaisModel dtoParaModel(DadosPessoaisDTO dto);

	  @lombok.Generated
	  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
	  @Mapping(target = "id", ignore = true)
	  @Mapping(target = "status", ignore = true)
	  void atualizaModelPeloDTO(DadosPessoaisDTO dto, @MappingTarget DadosPessoaisModel model);
}