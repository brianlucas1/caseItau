package br.com.itau.dto;

public record DadosPessoaisResponseDTO(
		
		Integer id,		
		String nome,
		String sobreNome,
		Integer idade, 
		String pais,
		String email,
		String status) {
	
}
