package br.com.educacao.gamificada.recompensa.entity;
import br.com.educacao.gamificada.entity.ConclusaoCursoEntity;
import br.com.educacao.gamificada.entity.AlunoEntity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "recompensas_premium")
public class RecompensaEntity {
    @Id
    private Long alunoId;
    @Column(nullable = false)
    private int vouchers;
    @Column(nullable = false)
    private int moedas;
    @Column(nullable = false)
    private String origem;
    @Column(nullable = false)
    private Instant registradoEm;

    protected RecompensaEntity() { }
    public RecompensaEntity(AlunoEntity aluno) {
        alunoId = aluno.getId();
        vouchers = aluno.getVouchers();
        moedas = aluno.getMoedas();
        origem = "PROMOCAO_PREMIUM";
        registradoEm = Instant.now();
    }
    public Long getAlunoId() { return alunoId; }
    public int getVouchers() { return vouchers; }
    public int getMoedas() { return moedas; }
    public String getOrigem() { return origem; }
    public Instant getRegistradoEm() { return registradoEm; }
}
