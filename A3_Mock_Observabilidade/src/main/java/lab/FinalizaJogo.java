package lab;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.stereotype.Service;
@Service public class FinalizaJogo {
 private final SalvarLote banco;
 private final Sms sms;
 private final Email email;
 public FinalizaJogo(SalvarLote banco,Sms sms,Email email) { this.banco=banco; this.sms=sms; this.email=email; }
 public int finalizar(List<Jogo> jogos, LocalDate hoje) {
  LocalDate fim=hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  LocalDate inicio=fim.minusWeeks(1);
  List<Finalizado> lote=jogos.stream().filter(j -> !j.data().isBefore(inicio) && j.data().isBefore(fim))
   .map(j -> new Finalizado(j.id(),j.vencedor())).toList();
  if(lote.isEmpty()) return 0;
  banco.salvar(lote);
  // Nao anotar este metodo com @Transactional: SMS deve ocorrer apos o commit do lote.
  // Uma falha externa nao deve impedir a tentativa dos outros avisos ja persistidos.
  var falhas=new java.util.ArrayList<RuntimeException>();
  for(var jogo:lote) {
   tentar(()->sms.enviar(jogo),falhas);
   tentar(()->email.enviar(jogo),falhas);
  }
  if(!falhas.isEmpty()) {
   var erro=new IllegalStateException("Jogos salvos; uma ou mais notificacoes falharam");
   falhas.forEach(erro::addSuppressed);
   throw erro;
  }
  return lote.size();
 }
 private void tentar(Runnable envio,java.util.List<RuntimeException> falhas) {
  try { envio.run(); } catch(RuntimeException falha) { falhas.add(falha); }
 }
}
