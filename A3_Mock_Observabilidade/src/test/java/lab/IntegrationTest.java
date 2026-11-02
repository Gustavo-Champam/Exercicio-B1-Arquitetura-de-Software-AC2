package lab;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest class IntegrationTest {
 @Autowired FinalizaJogo app; @Autowired JogosRepository repo; @MockBean Sms sms; @MockBean Email email;
 @Test void smsObservaDadosConfirmadosSemTransacaoAtiva() {
  doAnswer(call -> { assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
   assertEquals("Ana",repo.findById(99L).orElseThrow().getVencedor());return null;
  }).when(sms).enviar(any());
  assertEquals(1,app.finalizar(List.of(new Jogo(99,LocalDate.of(2026,10,20),List.of(new Jogo.Resultado("Ana",30)))),LocalDate.of(2026,10,26)));
  verify(sms).enviar(any()); verify(email).enviar(any());
 }
 @Test void adapterSmsDidaticoExecuta() { new SmsLog().enviar(new Finalizado(8,"Bia")); new EmailLog().enviar(new Finalizado(8,"Bia")); }
 @Test void falhaRealNoFlushReverteLoteENaoNotifica() {
  long antes=repo.count();
  var data=LocalDate.of(2026,10,20);
  var jogos=List.of(new Jogo(401,data,List.of(new Jogo.Resultado("Ana",30))),new Jogo(402,data,List.of(new Jogo.Resultado(null,10))));
  assertThrows(org.springframework.dao.DataIntegrityViolationException.class,()->app.finalizar(jogos,LocalDate.of(2026,10,26)));
  assertEquals(antes,repo.count());assertTrue(repo.findById(401L).isEmpty());
  verifyNoInteractions(sms,email);
 }
 @Test void bootstrapReal() { Application.main(new String[]{"--server.port=0","--spring.main.web-application-type=none"}); }
}
