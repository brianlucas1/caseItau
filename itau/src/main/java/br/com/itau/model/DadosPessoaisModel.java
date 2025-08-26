package br.com.itau.model;

import br.com.itau.enums.StatusCadastroEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_dados_pessoais")
public class DadosPessoaisModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;


	@Column(nullable = false)
	private String nome;


	@Column(nullable = false)
	private String sobreNome;


	@Column(nullable = false)
	private Integer idade;


	@Column(nullable = false)
	private String pais;

	@Column(nullable = false, unique = true)
	private String email;


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCadastroEnum status = StatusCadastroEnum.PENDENTE;

}
