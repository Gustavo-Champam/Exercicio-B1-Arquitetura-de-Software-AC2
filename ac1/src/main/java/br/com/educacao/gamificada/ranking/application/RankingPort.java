package br.com.educacao.gamificada.ranking.application;
import java.util.List;
public interface RankingPort {
 record Linha(long id,String nome,int cursos,int moedas) {}
 List<Linha> consultar();
}