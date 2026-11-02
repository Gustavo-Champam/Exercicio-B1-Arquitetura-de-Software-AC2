package br.com.educacao.gamificada.ranking;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import br.com.educacao.gamificada.service.AlunoService;
import br.com.educacao.gamificada.dto.*;
import br.com.educacao.gamificada.ranking.api.RankingController;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @Transactional class RankingIntegrationTest {
 @Autowired AlunoService alunos; @Autowired RankingController ranking;
 @Test void rankingUsaPromocaoERecompensasReaisDaAc1(){
  var aluno=alunos.criar(new CriarAlunoRequest("Aluno ranking"));
  for(int i=1;i<=12;i++)alunos.concluirCurso(aluno.id(),new ConcluirCursoRequest("R-"+i,8.0));
  var pos=ranking.listar().stream().filter(p->p.id()==aluno.id()).findFirst().orElseThrow();
  assertEquals(1203,pos.pontos());assertEquals("Aluno ranking",pos.nome());
 }
}
