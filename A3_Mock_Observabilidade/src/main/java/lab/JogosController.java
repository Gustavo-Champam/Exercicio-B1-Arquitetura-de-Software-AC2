package lab;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.MeterRegistry;
@RestController @RequestMapping("/jogos") public class JogosController {
 private final FinalizaJogo servico;
 private final MeterRegistry metrics;
 public JogosController(FinalizaJogo servico,MeterRegistry metrics) { this.servico=servico;this.metrics=metrics; }
 @PostMapping("/finalizar") public int finalizar(@RequestBody List<Jogo> jogos,@RequestParam LocalDate hoje) {
  int total=servico.finalizar(jogos,hoje);
  metrics.counter("jogos.finalizados").increment(total);
  return total;
 }
}
