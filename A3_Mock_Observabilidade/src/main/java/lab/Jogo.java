package lab;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
public record Jogo(long id, LocalDate data, List<Resultado> resultados) {
 public record Resultado(String participante, double pontos) {}
 public String vencedor() {
  return resultados.stream().max(Comparator.comparingDouble(Resultado::pontos)
   .thenComparing(Resultado::participante,Comparator.reverseOrder())).orElseThrow(
    () -> new IllegalStateException("Jogo sem resultados")).participante();
 }
}
