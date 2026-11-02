package br.com.educacao.gamificada.dto;

import br.com.educacao.gamificada.domain.Plano;
import br.com.educacao.gamificada.entity.AlunoEntity;

public record AlunoResponse(Long id, String nome, Plano plano, int cursosConcluidos, int vouchers, int moedas) {
    public static AlunoResponse from(AlunoEntity aluno) {
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getPlano(), aluno.getCursosConcluidos(), aluno.getVouchers(), aluno.getMoedas());
    }
}
