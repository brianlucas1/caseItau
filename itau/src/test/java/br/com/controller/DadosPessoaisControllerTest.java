package br.com.controller;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.itau.controller.DadosPessoaisController;
import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.dto.DadosPessoaisResponseDTO;
import br.com.itau.interfaces.DadosPessoaisService;
import br.com.itau.mapper.DadosPessoaisMapper;
import br.com.itau.model.DadosPessoaisModel;

class DadosPessoaisControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DadosPessoaisService service;

    @Mock
    private DadosPessoaisMapper mapper;

    @InjectMocks
    private DadosPessoaisController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.objectMapper = new ObjectMapper();
    }

    private DadosPessoaisResponseDTO resp(Integer id, String nome) {
        return new DadosPessoaisResponseDTO(
            id,
            nome,
            "Sobrenome",  
            30,           
            "BR",        
            "email@mail.com",
            "ATIVO"       
        );
    }

    private DadosPessoaisModel model(Integer id, String nome) {
        var m = new DadosPessoaisModel();
       
        try {
            var fId = DadosPessoaisModel.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(m, id);
        } catch (Exception ignored) {}
        try {
            var fNome = DadosPessoaisModel.class.getDeclaredField("nome");
            fNome.setAccessible(true);
            fNome.set(m, nome);
        } catch (Exception ignored) {}
        return m;
    }

    @Nested
    @DisplayName("POST /api/dados-pessoais")
    class PostCadastro {
        @Test
        @DisplayName("201 Created com corpo")
        void deveCriar() throws Exception {
            Map<String, Object> body = Map.of(
                "nome", "Ana",
                "sobreNome", "Silva",
                "idade", 28,
                "pais", "BR",
                "email", "ana@mail.com",
                "status", "ATIVO"
            );

            var retorno = resp(1, "Ana");
            given(service.cadastraNovoDadoPessoais(Mockito.any(DadosPessoaisDTO.class))).willReturn(retorno);

            mockMvc.perform(post("/api/dados-pessoais")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Ana")))
                .andExpect(jsonPath("$.email", is("email@mail.com"))); 
        }
    }

    @Nested
    @DisplayName("GET /api/dados-pessoais")
    class GetTodos {
        @Test
        @DisplayName("200 OK com Page<DadosPessoaisResponseDTO>")
        void deveListarPaginado() throws Exception {
            var dto1 = resp(1, "Ana");
            var dto2 = resp(2, "Bruno");

            Page<DadosPessoaisResponseDTO> page = new PageImpl<>(
                List.of(dto1, dto2),
                PageRequest.of(0, 10),
                2
            );

            given(service.buscaTodosCadastros(Mockito.any())).willReturn(page);

            mockMvc.perform(get("/api/dados-pessoais").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Ana")))
                .andExpect(jsonPath("$.totalElements", is(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/dados-pessoais/{id}")
    class GetPorId {
        @Test
        @DisplayName("200 OK com DTO mapeado do Model")
        void deveBuscarPorId() throws Exception {
            var model = model(10, "Carla");
            var dto = resp(10, "Carla");

            given(service.buscaCadastroPorId(10)).willReturn(model);
            given(mapper.modelParaDTO(model)).willReturn(dto);

            mockMvc.perform(get("/api/dados-pessoais/{id}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nome", is("Carla")));
        }
    }

    @Nested
    @DisplayName("PUT /api/dados-pessoais/{id}")
    class PutAtualiza {
        @Test
        @DisplayName("200 OK com recurso atualizado")
        void deveAtualizar() throws Exception {
            Map<String, Object> body = Map.of(
                "nome", "Diego",
                "sobreNome", "Souza",
                "idade", 31,
                "pais", "BR",
                "email", "diego@mail.com",
                "status", "ATIVO"
            );

            var atualizado = resp(5, "Diego");
            given(service.atualizaDadosPessoais(eq(5), Mockito.any(DadosPessoaisDTO.class))).willReturn(atualizado);

            mockMvc.perform(put("/api/dados-pessoais/{id}", 5)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nome", is("Diego")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/dados-pessoais/{id}")
    class DeletePorId {
        @Test
        @DisplayName("204 No Content")
        void deveDeletar() throws Exception {
            doNothing().when(service).deletarDadosPessoaisPorId(7);

            mockMvc.perform(delete("/api/dados-pessoais/{id}", 7))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("PATCH /api/dados-pessoais/{id}")
    class PatchDados {
        @Test
        @DisplayName("200 OK com parcial atualizado")
        void devePatch() throws Exception {
            Map<String, Object> updates = Map.of("email", "novo@mail.com");
            var patched = resp(3, "Eva");

            given(service.patchDadosPessoais(eq(3), Mockito.anyMap())).willReturn(patched);

            mockMvc.perform(patch("/api/dados-pessoais/{id}", 3)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nome", is("Eva")));
        }
    }


}

