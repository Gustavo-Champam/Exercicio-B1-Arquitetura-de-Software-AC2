package com.exemplo.usuario.ranking.internal.persistence;
import com.exemplo.usuario.ranking.application.RankingPort;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class RankingJpaAdapter implements RankingPort {
 private final RankingRepository repository;
 public RankingJpaAdapter(RankingRepository repository) { this.repository=repository; }
 public List<Linha> buscar() { return repository.buscarRanking().stream().map(a -> new Linha(a.getUsuario().getId(),a.getUsuario().getNome(),a.getCursosConcluidosComSucesso(),a.getMoedas())).toList(); }
}
