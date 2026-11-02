package br.com.educacao.gamificada.recompensa.repository;
import br.com.educacao.gamificada.repository.ConclusaoCursoRepository;
import br.com.educacao.gamificada.repository.AlunoRepository;
import br.com.educacao.gamificada.recompensa.entity.RecompensaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RecompensaRepository extends JpaRepository<RecompensaEntity, Long> { }
