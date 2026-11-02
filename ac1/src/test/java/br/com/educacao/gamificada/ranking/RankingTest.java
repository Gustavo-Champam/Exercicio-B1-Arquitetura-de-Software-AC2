package br.com.educacao.gamificada.ranking;
import org.junit.jupiter.api.Test;
import java.util.List;
import br.com.educacao.gamificada.ranking.application.*;
import br.com.educacao.gamificada.ranking.domain.Pontuacao;
import static org.junit.jupiter.api.Assertions.*;
class RankingTest {
 @Test void calculaEOrdenaComPortaFake(){
  var service=new RankingService(()->List.of(new RankingPort.Linha(2,"Bia",12,3),new RankingPort.Linha(1,"Ana",12,3),new RankingPort.Linha(3,"Caio",2,0)));
  assertEquals(List.of(1L,2L,3L),service.listar().stream().map(RankingService.Posicao::id).toList());
  assertEquals("Ana",service.listar().get(0).nome());assertEquals(1203,service.listar().get(0).pontos());
 }
 @Test void negativosEOverflow(){assertEquals(0,Pontuacao.calcular(0,0));
  assertThrows(IllegalArgumentException.class,()->Pontuacao.calcular(-1,0));
  assertThrows(IllegalArgumentException.class,()->Pontuacao.calcular(0,-1));
  assertThrows(ArithmeticException.class,()->Pontuacao.calcular(Integer.MAX_VALUE,0));
 }
}