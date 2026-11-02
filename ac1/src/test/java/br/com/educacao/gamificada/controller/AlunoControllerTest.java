package br.com.educacao.gamificada.controller;

import br.com.educacao.gamificada.event.AlunoPromovidoEvent;
import br.com.educacao.gamificada.repository.AlunoRepository;
import br.com.educacao.gamificada.repository.ConclusaoCursoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
class AlunoControllerTest {
    @Autowired MockMvc api;
    @Autowired ObjectMapper json;
    @Autowired AlunoRepository alunos;
    @Autowired ConclusaoCursoRepository conclusoes;
    @Autowired ApplicationEvents eventos;

    @BeforeEach
    void limparBanco() {
        conclusoes.deleteAll();
        alunos.deleteAll();
        eventos.clear();
    }

    @Test
    void deveCadastrarConsultarEListarAlunoBasico() throws Exception {
        long id = criarAluno();

        api.perform(get("/api/alunos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo"))
                .andExpect(jsonPath("$.plano").value("BASICO"))
                .andExpect(jsonPath("$.cursosConcluidos").value(0));
        api.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void devePersistirPromocaoNoDecimoSegundoCursoEEmitirUmEvento() throws Exception {
        long id = criarAluno();
        concluirOnzeCursos(id);

        concluir(id, "CURSO-12", 7.0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promovidoAgora").value(true))
                .andExpect(jsonPath("$.aluno.plano").value("PREMIUM"))
                .andExpect(jsonPath("$.aluno.cursosConcluidos").value(12));

        api.perform(get("/api/alunos/{id}", id))
                .andExpect(jsonPath("$.plano").value("PREMIUM"));
        assertEquals(12, conclusoes.count());
        assertEquals(1, eventos.stream(AlunoPromovidoEvent.class).count());
        assertEquals(id, eventos.stream(AlunoPromovidoEvent.class).findFirst().orElseThrow().alunoId().longValue());
    }

    @Test
    void naoDeveContarNotaBaixaEDevePermitirNovaConclusaoDoMesmoCurso() throws Exception {
        long id = criarAluno();
        concluirOnzeCursos(id);

        concluir(id, "CURSO-12", 6.99)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursoContabilizado").value(false))
                .andExpect(jsonPath("$.promovidoAgora").value(false))
                .andExpect(jsonPath("$.aluno.plano").value("BASICO"))
                .andExpect(jsonPath("$.aluno.cursosConcluidos").value(11));
        assertEquals(0, eventos.stream(AlunoPromovidoEvent.class).count());
        assertEquals(11, conclusoes.count());

        concluir(id, "CURSO-12", 8.0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promovidoAgora").value(true));
    }

    @Test
    void deveImpedirDuplicidadeMesmoComEspacosOuLetrasMinusculas() throws Exception {
        long id = criarAluno();
        concluir(id, " curso-1 ", 8.0).andExpect(status().isOk());

        concluir(id, "CURSO-1", 9.0).andExpect(status().isConflict());

        assertEquals(1, alunos.findById(id).orElseThrow().getCursosConcluidos());
        assertEquals(1, conclusoes.count());
        assertEquals(0, eventos.stream(AlunoPromovidoEvent.class).count());
    }

    @Test
    void devePermitirMesmoCursoParaAlunosDiferentes() throws Exception {
        long primeiroId = criarAluno();
        long segundoId = criarAluno();

        concluir(primeiroId, "CURSO-1", 7.0).andExpect(status().isOk());
        concluir(segundoId, "CURSO-1", 7.0).andExpect(status().isOk());

        assertEquals(2, conclusoes.count());
    }

    @Test
    void naoDeveReemitirEventoAposPromocao() throws Exception {
        long id = criarAluno();
        concluirOnzeCursos(id);
        concluir(id, "CURSO-12", 7.0).andExpect(status().isOk());

        concluir(id, "CURSO-13", 10.0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promovidoAgora").value(false))
                .andExpect(jsonPath("$.aluno.plano").value("PREMIUM"));

        assertEquals(1, eventos.stream(AlunoPromovidoEvent.class).count());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"codigoCurso\":\"C1\",\"media\":-1}",
            "{\"codigoCurso\":\"C1\",\"media\":11}",
            "{\"codigoCurso\":\"C1\"}",
            "{\"codigoCurso\":\" \",\"media\":7}",
            "{\"media\":7}"
    })
    void deveRejeitarConclusaoInvalidaSemPersistir(String corpo) throws Exception {
        long id = criarAluno();
        api.perform(post("/api/alunos/{id}/conclusoes", id)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
        assertEquals(0, conclusoes.count());
        assertEquals(0, alunos.findById(id).orElseThrow().getCursosConcluidos());
    }

    @Test
    void deveRejeitarNomeVazio() throws Exception {
        api.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", org.hamcrest.Matchers.containsString("nome")));
        assertEquals(0, alunos.count());
    }

    @Test
    void deveRetornar404ParaAlunoInexistente() throws Exception {
        api.perform(get("/api/alunos/9999999")).andExpect(status().isNotFound());
        concluir(9999999L, "CURSO-1", 8.0).andExpect(status().isNotFound());
    }

    @Test
    void deveExporDocumentacaoOpenApi() throws Exception {
        api.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/alunos']").exists());
    }

    @Test
    void bddUS02DevePersistirUmaRecompensaMesmoAposNovoCursoEDuplicata() throws Exception {
        // Dado um aluno Basico com onze cursos validos (US02 - Gustavo Champam).
        long id = criarAluno();
        api.perform(get("/api/alunos/{id}", id))
                .andExpect(jsonPath("$.vouchers").value(0))
                .andExpect(jsonPath("$.moedas").value(0));
        concluirOnzeCursos(id);
        // Quando conclui o decimo segundo curso e posteriormente outro curso.
        concluir(id, "CURSO-12", 7.0).andExpect(status().isOk())
                .andExpect(jsonPath("$.aluno.vouchers").value(1))
                .andExpect(jsonPath("$.aluno.moedas").value(3));
        concluir(id, "CURSO-13", 9.0).andExpect(status().isOk());
        concluir(id, "CURSO-12", 7.0).andExpect(status().isConflict());
        // Entao a consulta em outra requisicao mantem apenas um voucher e tres moedas.
        api.perform(get("/api/alunos/{id}", id))
                .andExpect(jsonPath("$.vouchers").value(1))
                .andExpect(jsonPath("$.moedas").value(3))
                .andExpect(jsonPath("$.cursosConcluidos").value(13));
    }

    private long criarAluno() throws Exception {
        var resposta = api.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Gustavo\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(resposta).get("id").asLong();
    }

    private ResultActions concluir(long id, String codigo, double media) throws Exception {
        return api.perform(post("/api/alunos/{id}/conclusoes", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("codigoCurso", codigo, "media", media))));
    }

    private void concluirOnzeCursos(long id) throws Exception {
        for (int numero = 1; numero <= 11; numero++) {
            concluir(id, "CURSO-" + numero, 8.0)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.aluno.plano").value("BASICO"))
                    .andExpect(jsonPath("$.aluno.cursosConcluidos").value(numero))
                    .andExpect(jsonPath("$.promovidoAgora").value(false));
        }
    }
}
