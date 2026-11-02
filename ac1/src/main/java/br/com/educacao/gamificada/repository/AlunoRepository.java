package br.com.educacao.gamificada.repository;

import br.com.educacao.gamificada.entity.AlunoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {
    // Serializa conclusões concorrentes do mesmo aluno dentro da transação.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select aluno from AlunoEntity aluno where aluno.id = :id")
    Optional<AlunoEntity> buscarParaAtualizar(@Param("id") Long id);
}
