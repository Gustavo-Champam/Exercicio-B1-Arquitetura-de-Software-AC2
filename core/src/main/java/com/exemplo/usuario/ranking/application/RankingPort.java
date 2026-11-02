package com.exemplo.usuario.ranking.application;
import java.util.List;
public interface RankingPort {
 record Linha(Long id,String nome,int cursos,int moedas) {}
 List<Linha> buscar();
}
