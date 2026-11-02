package br.com.educacao.gamificada.controller;

import br.com.educacao.gamificada.recompensa.dto.RecompensaResponse;
import br.com.educacao.gamificada.dto.*;
import br.com.educacao.gamificada.event.AlunoPromovidoEvent;
import br.com.educacao.gamificada.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecompensaAceitacaoTest {
    @Autowired AlunoService servico;
    @Autowired MockMvc api;
    @Autowired ApplicationEventPublisher eventos;

    @Test
    void bddUS02HistoricoRegistraOrigemDosDepositosUmaUnicaVez() throws Exception {
        var aluno = servico.criar(new CriarAlunoRequest("Gustavo Champam - carteira"));
        eventos.publishEvent(new AlunoPromovidoEvent(aluno.id()));
        api.perform(get("/api/alunos/{id}/recompensas", aluno.id()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        for (int i = 1; i <= 12; i++) {
            servico.concluirCurso(aluno.id(), new ConcluirCursoRequest("HIST-" + i, 7.0));
        }
        eventos.publishEvent(new AlunoPromovidoEvent(aluno.id()));
        eventos.publishEvent(new AlunoPromovidoEvent(aluno.id()));
        api.perform(get("/api/alunos/{id}/recompensas", aluno.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].alunoId").value(aluno.id()))
                .andExpect(jsonPath("$[0].origem").value("PROMOCAO_PREMIUM"))
                .andExpect(jsonPath("$[0].vouchers").value(1))
                .andExpect(jsonPath("$[0].moedas").value(3))
                .andExpect(jsonPath("$[0].registradoEm").isNotEmpty());
    }

    @Test
    void deveRejeitarConsultaOuEventoParaAlunoInexistente() throws Exception {
        api.perform(get("/api/alunos/99999999/recompensas")).andExpect(status().isNotFound());
        assertThrows(ResponseStatusException.class,
                () -> eventos.publishEvent(new AlunoPromovidoEvent(99999999L)));
    }
}
