package lab;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;
class BrevoEmailTest {
 @Test void enviaAoVencedorComContratoBrevo() throws Exception {
  var builder=RestClient.builder(); var server=MockRestServiceServer.bindTo(builder).build();
  var email=new BrevoEmail(builder,"chave-de-teste","remetente@example.com","{\"Ana\":\"ana@example.com\"}");
  server.expect(requestTo("https://api.brevo.com/v3/smtp/email"))
   .andExpect(header("api-key","chave-de-teste"))
   .andExpect(jsonPath("$.to[0].email").value("ana@example.com"))
   .andExpect(jsonPath("$.textContent").value("Parabens, Ana! Voce venceu o jogo 1."))
   .andRespond(withStatus(HttpStatus.CREATED));
  email.enviar(new Finalizado(1,"Ana")); server.verify();
 }
 @Test void rejeicaoDoProvedorNaoViraSucesso() throws Exception {
  var builder=RestClient.builder(); var server=MockRestServiceServer.bindTo(builder).build();
  var email=new BrevoEmail(builder,"teste","a@example.com","{\"Ana\":\"ana@example.com\"}");
  server.expect(requestTo("https://api.brevo.com/v3/smtp/email")).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
  assertThrows(org.springframework.web.client.HttpClientErrorException.class,()->email.enviar(new Finalizado(1,"Ana")));server.verify();
 }
 @Test void contatoAusenteNaoFazRequisicao() throws Exception {
  var builder=RestClient.builder();var server=MockRestServiceServer.bindTo(builder).build();
  var email=new BrevoEmail(builder,"teste","a@example.com","{}");
  assertThrows(NullPointerException.class,()->email.enviar(new Finalizado(1,"Ana")));server.verify();
 }
}
