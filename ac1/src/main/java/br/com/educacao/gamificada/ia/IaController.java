package br.com.educacao.gamificada.ia;
import org.springframework.web.bind.annotation.*;
@RestController public class IaController {
 private final RecomendacaoClient client;
 public IaController(RecomendacaoClient client){this.client=client;}
 @GetMapping(value="/api/recomendacoes",produces="application/json") public String recomendar(){return client.recomendar();}
}