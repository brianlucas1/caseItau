package br.com.itau.dto;

public record DadosPessoaisDTO(
		
		String nome,
		String sobreNome,
		Integer idade, 
		String pais,
		String email,
		String status) {
	
}
