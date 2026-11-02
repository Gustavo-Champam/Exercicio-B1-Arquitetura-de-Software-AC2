package br.com.educacao.gamificada.ranking.api;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import br.com.educacao.gamificada.ranking.application.RankingService;
@RestController public class RankingController {
 private final RankingService service;
 public RankingController(RankingService service){this.service=service;}
 @GetMapping("/api/ranking") public List<RankingService.Posicao> listar(){return service.listar();}
}