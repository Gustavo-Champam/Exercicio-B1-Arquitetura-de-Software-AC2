package br.com.educacao.gamificada.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AlunoTest {

    @Test
    void devePromoverAlunoAoConcluirDecimoSegundoCursoComNotaValida() {
        var aluno = new Aluno("Gustavo", Plano.BASICO, 11);

        boolean promovido = aluno.concluirCurso(7.0);

        assertTrue(promovido);
        assertEquals(Plano.PREMIUM, aluno.getPlano());
        assertEquals(12, aluno.getCursosConcluidos());
    }

    @Test
    void naoDevePromoverAlunoAntesDeDozeCursos() {
        var aluno = new Aluno("Gustavo", Plano.BASICO, 10);

        assertFalse(aluno.concluirCurso(10.0));
        assertEquals(Plano.BASICO, aluno.getPlano());
        assertEquals(11, aluno.getCursosConcluidos());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 6.99})
    void naoDeveContabilizarCursoNemPromoverAlunoComNotaInferiorASete(double media) {
        var aluno = new Aluno("Gustavo", Plano.BASICO, 11);

        assertFalse(aluno.concluirCurso(media));
        assertEquals(Plano.BASICO, aluno.getPlano());
        assertEquals(11, aluno.getCursosConcluidos());
    }

    @Test
    void naoDevePromoverNovamenteAlunoQueJaEhPremium() {
        var aluno = new Aluno("Gustavo", Plano.PREMIUM, 12);

        assertFalse(aluno.concluirCurso(8.0));
        assertEquals(Plano.PREMIUM, aluno.getPlano());
        assertEquals(13, aluno.getCursosConcluidos());
    }

    @Test
    void devePermitirConsultarOsDadosDoAluno() {
        var aluno = new Aluno("Gustavo", Plano.BASICO, 0);

        assertEquals("Gustavo", aluno.getNome());
        assertEquals(Plano.BASICO, aluno.getPlano());
        assertEquals(0, aluno.getCursosConcluidos());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 10.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void deveRejeitarMediaInvalidaSemAlterarAluno(double media) {
        var aluno = new Aluno("Gustavo", Plano.BASICO, 11);

        assertThrows(IllegalArgumentException.class, () -> aluno.concluirCurso(media));
        assertEquals(11, aluno.getCursosConcluidos());
        assertEquals(Plano.BASICO, aluno.getPlano());
    }

    @Test
    void deveRejeitarDadosInvalidosNaCriacao() {
        assertThrows(IllegalArgumentException.class, () -> new Aluno(null, Plano.BASICO, 0));
        assertThrows(IllegalArgumentException.class, () -> new Aluno("  ", Plano.BASICO, 0));
        assertThrows(IllegalArgumentException.class, () -> new Aluno("Gustavo", null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Aluno("Gustavo", Plano.BASICO, -1));
    }
}
