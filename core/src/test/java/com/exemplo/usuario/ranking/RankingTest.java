package com.exemplo.usuario.ranking;
import com.exemplo.usuario.ranking.domain.Pontuacao;
import com.exemplo.usuario.ranking.application.*;
import com.exemplo.usuario.ranking.internal.persistence.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
class RankingTest {
 @Test void dominioSemSpring() { assertEquals(320,Pontuacao.calcular(3,20));assertThrows(IllegalArgumentException.class,()->Pontuacao.calcular(-1,0));assertThrows(IllegalArgumentException.class,()->Pontuacao.calcular(0,-1));assertThrows(ArithmeticException.class,()->Pontuacao.calcular(Integer.MAX_VALUE,0)); }
 @Test void adapterEmMemoria() { RankingPort fake=()->List.of(new RankingPort.Linha(1L,"Ana",3,20));assertEquals(320,new RankingService(fake).listar().get(0).getPontos()); }
 @Test void trocaPorAdapterJpaSemAlterarServico() { RankingRepository repo=mock(RankingRepository.class);when(repo.buscarRanking()).thenReturn(List.of());assertTrue(new RankingService(new RankingJpaAdapter(repo)).listar().isEmpty());verify(repo).buscarRanking(); }
}
