package lab;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;
class TwilioSmsTest {
 final RestClient.Builder b=RestClient.builder();
 final MockRestServiceServer server=MockRestServiceServer.bindTo(b).build();
 TwilioSms adapter(String contatos) throws Exception {return new TwilioSms(b,"ACteste","token","+15550000001",contatos,"Modelo aprovado no console");}
 @Test void trialEnviaModeloSemAlteracao() throws Exception {
  var sms=adapter("{\"Ana\":\"+15550000002\"}");
  server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/ACteste.json"))
   .andExpect(header("Authorization","Basic QUN0ZXN0ZTp0b2tlbg=="))
   .andRespond(withSuccess("{\"type\":\"Trial\"}",MediaType.APPLICATION_JSON));
  server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/ACteste/Messages.json"))
   .andExpect(method(HttpMethod.POST)).andExpect(content().string("From=%2B15550000001&To=%2B15550000002&Body=Modelo+aprovado+no+console"))
   .andRespond(withStatus(HttpStatus.CREATED));
  sms.enviar(new Finalizado(1,"Ana"));server.verify();
 }
 @Test void contaPagaBloqueiaSms() throws Exception {
  var sms=adapter("{\"Ana\":\"+15550000002\"}");
  server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/ACteste.json"))
   .andRespond(withSuccess("{\"type\":\"Full\"}",MediaType.APPLICATION_JSON));
  assertThrows(IllegalStateException.class,()->sms.enviar(new Finalizado(1,"Ana")));server.verify();
 }
 @Test void semContatoNaoAcessaProvedor() throws Exception {
  var sms=adapter("{}");assertThrows(NullPointerException.class,()->sms.enviar(new Finalizado(1,"Ana")));server.verify();
 }
 @Test void erroDoProvedorNaoViraSucesso() throws Exception {
  var sms=adapter("{\"Ana\":\"+15550000002\"}");
  server.expect(requestTo("https://api.twilio.com/2010-04-01/Accounts/ACteste.json")).andRespond(withStatus(HttpStatus.UNAUTHORIZED));
  assertThrows(org.springframework.web.client.HttpClientErrorException.class,()->sms.enviar(new Finalizado(1,"Ana")));server.verify();
 }
}
