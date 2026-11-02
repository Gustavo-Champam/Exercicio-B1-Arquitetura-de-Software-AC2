package br.com.educacao.gamificada.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "conclusoes_cursos", uniqueConstraints =
        @UniqueConstraint(name = "uk_aluno_curso", columnNames = {"aluno_id", "codigo_curso"}))
public class ConclusaoCursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private AlunoEntity aluno;

    @Column(name = "codigo_curso", nullable = false, length = 80)
    private String codigoCurso;

    @Column(nullable = false)
    private double media;

    protected ConclusaoCursoEntity() { }

    public ConclusaoCursoEntity(AlunoEntity aluno, String codigoCurso, double media) {
        this.aluno = aluno;
        this.codigoCurso = codigoCurso;
        this.media = media;
    }
}
