package br.com.itau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.itau.model.DadosPessoaisModel;

@Repository
public interface  DadosPessoaisRepository extends JpaRepository<DadosPessoaisModel, Integer> {

	boolean existsByNomeAndSobreNomeAndIdadeAndPais(String nome, String sobreNome, Integer idade, String pais);

	boolean existsByEmail(String email);

}
