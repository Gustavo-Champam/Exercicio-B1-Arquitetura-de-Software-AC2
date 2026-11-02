package lab;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
class FinalizaJogoTest {
 final SalvarLote banco=mock(SalvarLote.class);
 final Sms sms=mock(Sms.class);
 final Email email=mock(Email.class);
 final FinalizaJogo app=new FinalizaJogo(banco,sms,email);
 final LocalDate hoje=LocalDate.of(2026,10,26);
 Jogo jogo(long id,String data) { return new Jogo(id,LocalDate.parse(data),List.of(new Jogo.Resultado("Ana",20),new Jogo.Resultado("Bia",10))); }
 @Test void salvaTodosAntesDeQualquerSms() {
  assertEquals(2,app.finalizar(List.of(jogo(1,"2026-10-19"),jogo(2,"2026-10-25")),hoje));
  var ordem=inOrder(banco,sms,email);
  ordem.verify(banco).salvar(argThat(l -> l.size()==2 && l.get(0).getId()==1 && l.get(0).getVencedor().equals("Ana")));
  ordem.verify(sms,times(2)).enviar(any()); ordem.verify(email,times(2)).enviar(any()); ordem.verifyNoMoreInteractions();
 }
 @Test void falhaAoSalvarNaoEnviaSms() {
  doThrow(new IllegalStateException("falha no commit")).when(banco).salvar(anyList());
  assertThrows(IllegalStateException.class,()->app.finalizar(List.of(jogo(1,"2026-10-20")),hoje));
  verifyNoInteractions(sms,email);
 }
 @Test void respeitaIntervaloCalendario() {
  assertEquals(0,app.finalizar(List.of(jogo(1,"2026-10-18"),jogo(2,"2026-10-26"),jogo(3,"2026-11-01")),hoje));
  verifyNoInteractions(banco,sms,email);
  assertEquals(0,app.finalizar(List.of(),hoje));
 }
 @Test void semResultadoAbortaLoteInteiro() {
  assertThrows(IllegalStateException.class,()->app.finalizar(List.of(jogo(1,"2026-10-20"),new Jogo(2,LocalDate.of(2026,10,21),List.of())),hoje));
  verifyNoInteractions(banco,sms,email);
 }
 @Test void empateUsaNomeEmOrdemAlfabetica() {
  var j=new Jogo(3,hoje,List.of(new Jogo.Resultado("Bia",10),new Jogo.Resultado("Ana",10)));
  assertEquals("Ana",j.vencedor());
 }
 @Test void falhaEmailAconteceDepoisDoBanco() {
  doThrow(new IllegalStateException("email indisponivel")).when(email).enviar(any());
  assertThrows(IllegalStateException.class,()->app.finalizar(List.of(jogo(1,"2026-10-20")),hoje));
  var ordem=inOrder(banco,sms,email);ordem.verify(banco).salvar(anyList());ordem.verify(sms).enviar(any());ordem.verify(email).enviar(any());
 }
 @Test void falhaSmsOcorreDepoisDaPersistencia() {
  doThrow(new IllegalStateException("provedor indisponivel")).when(sms).enviar(any());
  assertThrows(IllegalStateException.class,()->app.finalizar(List.of(jogo(1,"2026-10-20")),hoje));
  var ordem=inOrder(banco,sms,email);ordem.verify(banco).salvar(anyList());ordem.verify(sms).enviar(any());
 }
}
