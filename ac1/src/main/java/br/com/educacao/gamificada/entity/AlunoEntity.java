package br.com.educacao.gamificada.entity;

import br.com.educacao.gamificada.domain.Aluno;
import br.com.educacao.gamificada.domain.Plano;
import jakarta.persistence.*;

@Entity
@Table(name = "alunos")
public class AlunoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Plano plano;

    @Column(name = "cursos_concluidos", nullable = false)
    private int cursosConcluidos;

    @Column(nullable = false)
    private int vouchers;

    @Column(nullable = false)
    private int moedas;

    protected AlunoEntity() { }

    public AlunoEntity(Aluno aluno) {
        nome = aluno.getNome();
        atualizarProgressao(aluno);
    }

    public Aluno toDomain() {
        return new Aluno(nome, plano, cursosConcluidos);
    }

    public void atualizarProgressao(Aluno aluno) {
        plano = aluno.getPlano();
        cursosConcluidos = aluno.getCursosConcluidos();
        vouchers = aluno.getVouchers();
        moedas = aluno.getMoedas();
    }

    public int getVouchers() { return vouchers; }
    public int getMoedas() { return moedas; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Plano getPlano() { return plano; }
    public int getCursosConcluidos() { return cursosConcluidos; }
}
