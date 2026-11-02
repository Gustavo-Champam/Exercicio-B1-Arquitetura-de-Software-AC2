package br.com.educacao.gamificada.ranking.application;
import java.util.*;
import org.springframework.stereotype.Service;
import br.com.educacao.gamificada.ranking.domain.Pontuacao;
@Service public class RankingService {
 private final RankingPort port;
 public RankingService(RankingPort port){this.port=port;}
 public record Posicao(long id,String nome,int pontos) {}
 public List<Posicao> listar(){return port.consultar().stream()
 .map(l->new Posicao(l.id(),l.nome(),Pontuacao.calcular(l.cursos(),l.moedas())))
 .sorted(Comparator.comparingInt(Posicao::pontos).reversed().thenComparingLong(Posicao::id)).toList();}
}