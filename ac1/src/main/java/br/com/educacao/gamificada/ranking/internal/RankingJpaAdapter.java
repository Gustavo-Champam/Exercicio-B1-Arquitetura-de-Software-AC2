package br.com.educacao.gamificada.ranking.internal;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import br.com.educacao.gamificada.repository.AlunoRepository;
import br.com.educacao.gamificada.ranking.application.RankingPort;
@Component public class RankingJpaAdapter implements RankingPort {
 private final AlunoRepository alunos;
 public RankingJpaAdapter(AlunoRepository alunos){this.alunos=alunos;}
 @Transactional(readOnly=true) public List<Linha> consultar(){return alunos.findAll().stream()
 .map(a->new Linha(a.getId(),a.getNome(),a.getCursosConcluidos(),a.getMoedas())).toList();}
}