package br.com.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import br.com.itau.interfaces.NotificacaoService;
import br.com.itau.mapper.DadosPessoaisMapper;
import br.com.itau.model.DadosPessoaisModel;
import br.com.itau.repository.DadosPessoaisRepository;
import br.com.itau.service.DadosPessoaisServiceImpl;

class DadosPessoaisServiceImplTest {

    @Mock
    private DadosPessoaisRepository repo;

    @Mock
    private DadosPessoaisMapper mapper;

    @Mock
    private NotificacaoService notificacao;

    @InjectMocks
    private DadosPessoaisServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===== Helpers =====
    private DadosPessoaisDTO dto(String nome, String email) {
        return new DadosPessoaisDTO(nome, "Sobrenome", 30, "BR", email, "ATIVO");
    }

    private DadosPessoaisModel model(Integer id, String nome, String email) {
        DadosPessoaisModel m = new DadosPessoaisModel();
        m.setId(id);
        m.setNome(nome);
        m.setSobreNome("Sobrenome");
        m.setIdade(30);
        m.setPais("BR");
        m.setEmail(email);
        return m;
    }

    private DadosPessoaisResponseDTO resp(Integer id, String nome, String email) {
        return new DadosPessoaisResponseDTO(id, nome, "Sobrenome", 30, "BR", email, "ATIVO");
    }

    // ===== cadastraNovoDadoPessoais =====
    @Test
    @DisplayName("cadastraNovoDadoPessoais: deve salvar, notificar e retornar DTO")
    void cadastra_ok() throws Exception {
        var dto = dto("Ana", "ana@mail.com");
        var entity = model(null, "Ana", "ana@mail.com");
        var saved = model(1, "Ana", "ana@mail.com");
        var expected = resp(1, "Ana", "ana@mail.com");

        given(repo.existsByEmail("ana@mail.com")).willReturn(false);
        given(mapper.dtoParaModel(dto)).willReturn(entity);
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var result = service.cadastraNovoDadoPessoais(dto);

        assertEquals(1, result.id());
        assertEquals("Ana", result.nome());
        verify(notificacao, times(1)).enviarNotificacaoCadastroPendente(dto);
    }

    @Test
    @DisplayName("cadastraNovoDadoPessoais: deve lançar exceção se e-mail já existir")
    void cadastra_emailDuplicado() {
        var dto = dto("Ana", "ana@mail.com");
        given(repo.existsByEmail("ana@mail.com")).willReturn(true);

        var ex = assertThrows(DadosPessoaisExcpetion.class,
                () -> service.cadastraNovoDadoPessoais(dto));
        assertTrue(ex.getMessage().contains("Email já cadastrado"));
        verify(notificacao, times(0)).enviarNotificacaoCadastroPendente(any());
        verify(repo, times(0)).save(any());
    }

    // ===== buscaCadastroPorId =====
    @Test
    @DisplayName("buscaCadastroPorId: deve retornar entidade quando existir")
    void buscaPorId_ok() throws Exception {
        var entity = model(10, "Carla", "carla@mail.com");
        given(repo.findById(10)).willReturn(Optional.of(entity));

        var out = service.buscaCadastroPorId(10);

        assertNotNull(out);
        assertEquals(10, out.getId());
    }

    @Test
    @DisplayName("buscaCadastroPorId: deve lançar exceção quando não existir")
    void buscaPorId_naoEncontrado() {
        given(repo.findById(99)).willReturn(Optional.empty());

        assertThrows(DadosPessoaisExcpetion.class, () -> service.buscaCadastroPorId(99));
    }


    // ===== atualizaDadosPessoais =====
    @Test
    @DisplayName("atualizaDadosPessoais: deve buscar, atualizar via mapper e salvar")
    void atualiza_ok() throws Exception {
        var dto = dto("Diego", "diego@mail.com");
        var entity = model(5, "Antigo", "antigo@mail.com");
        var saved = model(5, "Diego", "diego@mail.com");
        var expected = resp(5, "Diego", "diego@mail.com");

        given(repo.findById(5)).willReturn(Optional.of(entity));
        willDoNothing().given(mapper).atualizaModelPeloDTO(dto, entity);
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.atualizaDadosPessoais(5, dto);

        assertEquals(5, out.id());
        assertEquals("Diego", out.nome());
        verify(mapper).atualizaModelPeloDTO(dto, entity);
    }

    // ===== deletarDadosPessoaisPorId =====
    @Test
    @DisplayName("deletarDadosPessoaisPorId: deve deletar quando existir")
    void deletar_ok() throws Exception {
        given(repo.existsById(7)).willReturn(true);
        willDoNothing().given(repo).deleteById(7);

        assertDoesNotThrow(() -> service.deletarDadosPessoaisPorId(7));
        verify(repo).deleteById(7);
    }

    @Test
    @DisplayName("deletarDadosPessoaisPorId: deve lançar exceção quando não existir")
    void deletar_naoEncontrado() {
        given(repo.existsById(123)).willReturn(false);

        var ex = assertThrows(DadosPessoaisExcpetion.class,
                () -> service.deletarDadosPessoaisPorId(123));
        assertTrue(ex.getMessage().contains("Não foi excluir"));
        verify(repo, times(0)).deleteById(anyInt());
    }

    // ===== patchDadosPessoais =====
    @Test
    @DisplayName("patch: deve lançar se updates nulos ou vazios")
    void patch_semCampos() {
        assertThrows(DadosPessoaisExcpetion.class,
                () -> service.patchDadosPessoais(1, null));
        assertThrows(DadosPessoaisExcpetion.class,
                () -> service.patchDadosPessoais(1, Map.of()));
    }

    @Test
    @DisplayName("patch: deve lançar se mais de 1 campo enviado")
    void patch_muitosCampos() {
        assertThrows(DadosPessoaisExcpetion.class,
                () -> service.patchDadosPessoais(1, Map.of("nome", "X", "email", "x@mail.com")));
    }

    @Test
    @DisplayName("patch: deve atualizar nome")
    void patch_nome_ok() throws Exception {
        var entity = model(3, "Old", "old@mail.com");
        var saved = model(3, "Novo", "old@mail.com");
        var expected = resp(3, "Novo", "old@mail.com");

        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.patchDadosPessoais(3, Map.of("nome", "Novo"));

        assertEquals("Novo", out.nome());
    }

    @Test
    @DisplayName("patch: deve atualizar sobrenome")
    void patch_sobrenome_ok() throws Exception {
        var entity = model(3, "Nome", "mail@mail.com");
        var saved = model(3, "Nome", "mail@mail.com");
        saved.setSobreNome("Atualizado");
        var expected = new DadosPessoaisResponseDTO(3, "Nome", "Atualizado", 30, "BR", "mail@mail.com", "ATIVO");

        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.patchDadosPessoais(3, Map.of("sobrenome", "Atualizado"));

        assertEquals("Atualizado", out.sobreNome());
    }

    @Test
    @DisplayName("patch: deve atualizar idade")
    void patch_idade_ok() throws Exception {
        var entity = model(3, "Nome", "mail@mail.com");
        var saved = model(3, "Nome", "mail@mail.com");
        saved.setIdade(35);
        var expected = new DadosPessoaisResponseDTO(3, "Nome", "Sobrenome", 35, "BR", "mail@mail.com", "ATIVO");

        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.patchDadosPessoais(3, Map.of("idade", 35));

        assertEquals(35, out.idade());
    }

    @Test
    @DisplayName("patch: deve atualizar pais")
    void patch_pais_ok() throws Exception {
        var entity = model(3, "Nome", "mail@mail.com");
        var saved = model(3, "Nome", "mail@mail.com");
        saved.setPais("US");
        var expected = new DadosPessoaisResponseDTO(3, "Nome", "Sobrenome", 30, "US", "mail@mail.com", "ATIVO");

        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.patchDadosPessoais(3, Map.of("pais", "US"));

        assertEquals("US", out.pais());
    }

    
    @Test
    @DisplayName("buscaTodosCadastros: repassa Pageable ao repo e mapeia corretamente")
    void devePaginarEMapear() {
        // given
        var pageable = PageRequest.of(1, 5); // página 2, size 5
        var e1 = model(1, "Ana", "a@mail.com");
        var e2 = model(2, "Bruno", "b@mail.com");

        var pageEntities = new PageImpl<>(List.of(e1, e2), pageable, 2L);
        given(repo.findAll(any(PageRequest.class))).willReturn(pageEntities);
        given(mapper.modelParaDTO(e1)).willReturn(resp(1, "Ana", "a@mail.com"));
        given(mapper.modelParaDTO(e2)).willReturn(resp(2, "Bruno", "b@mail.com"));

        // when
        Page<DadosPessoaisResponseDTO> result = service.buscaTodosCadastros(pageable);

        // then: conteúdo mapeado
        assertEquals(7, result.getTotalElements());
        assertEquals("Ana", result.getContent().get(0).nome());
        assertEquals("Bruno", result.getContent().get(1).nome());

        // then: pageable repassado certinho ao repo
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(repo).findAll(captor.capture());
        PageRequest enviado = captor.getValue();
        assertEquals(1, enviado.getPageNumber());
        assertEquals(5, enviado.getPageSize());

        // mapper chamado para cada elemento
        verify(mapper, times(1)).modelParaDTO(e1);
        verify(mapper, times(1)).modelParaDTO(e2);
    }

    
    @Test
    @DisplayName("patch: deve atualizar e-mail quando não duplicado")
    void patch_email_ok() throws Exception {
        var entity = model(3, "Nome", "old@mail.com");
        var saved = model(3, "Nome", "new@mail.com");
        var expected = new DadosPessoaisResponseDTO(3, "Nome", "Sobrenome", 30, "BR", "new@mail.com", "ATIVO");

        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.existsByEmail("new@mail.com")).willReturn(false);
        given(repo.save(entity)).willReturn(saved);
        given(mapper.modelParaDTO(saved)).willReturn(expected);

        var out = service.patchDadosPessoais(3, Map.of("email", "new@mail.com"));

        assertEquals("new@mail.com", out.email());
    }

    @Test
    @DisplayName("patch: deve lançar se e-mail novo já existir")
    void patch_emailDuplicado() {
        var entity = model(3, "Nome", "old@mail.com");
        given(repo.findById(3)).willReturn(Optional.of(entity));
        given(repo.existsByEmail("dup@mail.com")).willReturn(true);

        var ex = assertThrows(DadosPessoaisExcpetion.class,
                () -> service.patchDadosPessoais(3, Map.of("email", "dup@mail.com")));
        assertTrue(ex.getMessage().contains("Email já cadastrado"));
        verify(repo, times(0)).save(any());
    }

    @Test
    @DisplayName("patch: deve lançar para campo não suportado")
    void patch_campoNaoSuportado() {
        var entity = model(3, "Nome", "mail@mail.com");
        given(repo.findById(3)).willReturn(Optional.of(entity));

        var ex = assertThrows(DadosPessoaisExcpetion.class,
                () -> service.patchDadosPessoais(3, Map.of("foo", "bar")));
        assertTrue(ex.getMessage().contains("Campo não suportado"));
    }
}
