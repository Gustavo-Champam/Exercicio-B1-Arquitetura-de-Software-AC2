package br.com.educacao.gamificada.repository;

import br.com.educacao.gamificada.entity.ConclusaoCursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConclusaoCursoRepository extends JpaRepository<ConclusaoCursoEntity, Long> {
    boolean existsByAluno_IdAndCodigoCurso(Long alunoId, String codigoCurso);
}
