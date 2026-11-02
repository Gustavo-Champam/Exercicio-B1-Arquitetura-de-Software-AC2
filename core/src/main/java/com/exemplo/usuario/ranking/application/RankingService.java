package com.exemplo.usuario.ranking.application;
import com.exemplo.usuario.ranking.api.RankingResponseDTO;
import com.exemplo.usuario.ranking.domain.Pontuacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service public class RankingService {
 private final RankingPort port;
 public RankingService(RankingPort port) { this.port=port; }
 @Transactional(readOnly=true) public List<RankingResponseDTO> listar() {
  return port.buscar().stream().map(a -> new RankingResponseDTO(a.id(),a.nome(),a.cursos(),a.moedas(),Pontuacao.calcular(a.cursos(),a.moedas()))).toList();
 }
}
