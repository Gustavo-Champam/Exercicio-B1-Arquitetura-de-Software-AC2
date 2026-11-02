package br.com.educacao.gamificada.domaintest;

import br.com.educacao.gamificada.domain.Aluno;
import br.com.educacao.gamificada.domain.Plano;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlunoPremiumTest {

    @Test
    void deveReceberUmVoucherAoVirarPremium() {
        Aluno aluno = new Aluno("Gustavo", Plano.PREMIUM, 12);

        assertEquals(1, aluno.getVouchers());
    }

    @Test
    void deveReceberTresMoedasAoVirarPremium() {
        Aluno aluno = new Aluno("Gustavo", Plano.PREMIUM, 12);

        assertEquals(3, aluno.getMoedas());
    }

    @Test
    void naoDeveReceberRecompensaDuasVezes() {
        Aluno aluno = new Aluno("Gustavo", Plano.PREMIUM, 12);

        aluno.concederRecompensasPremium();
        aluno.concederRecompensasPremium();

        assertEquals(1, aluno.getVouchers());
        assertEquals(3, aluno.getMoedas());
    }
    @Test
    void deveConcederRecompensasNaPromocaoEManterAposOutroCurso() {
        Aluno aluno = new Aluno("Gustavo", Plano.BASICO, 11);
        aluno.concluirCurso(7.0);
        assertEquals(1, aluno.getVouchers());
        assertEquals(3, aluno.getMoedas());
        aluno.concluirCurso(9.0);
        aluno.concederRecompensasPremium();
        assertEquals(1, aluno.getVouchers());
        assertEquals(3, aluno.getMoedas());
    }

    @Test
    void basicoNaoRecebeRecompensasNemComSolicitacaoDireta() {
        Aluno aluno = new Aluno("Gustavo", Plano.BASICO, 11);
        aluno.concederRecompensasPremium();
        aluno.concluirCurso(6.99);
        assertEquals(0, aluno.getVouchers());
        assertEquals(0, aluno.getMoedas());
    }
}
