package lab;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
@Component @Profile("sms-trial")
public class TwilioSms implements Sms {
 private final RestClient client;
 private final String sid,from,modelo;
 private final Map<String,String> contatos;
 public TwilioSms(RestClient.Builder builder,@Value("${TWILIO_ACCOUNT_SID}") String sid,
  @Value("${TWILIO_AUTH_TOKEN}") String token,@Value("${TWILIO_FROM}") String from,
  @Value("${TWILIO_DESTINATARIOS}") String destinos,@Value("${TWILIO_TRIAL_MESSAGE}") String modelo) throws Exception {
  this.sid=sid;this.from=from;this.modelo=modelo;
  this.contatos=new ObjectMapper().readValue(destinos,new TypeReference<Map<String,String>>(){});
  this.client=builder.baseUrl("https://api.twilio.com/2010-04-01")
   .defaultHeaders(h->h.setBasicAuth(sid,token)).build();
 }
 public void enviar(Finalizado jogo) {
  String to=Objects.requireNonNull(contatos.get(jogo.getVencedor()),"Vencedor sem telefone cadastrado");
  Map<?,?> conta=Objects.requireNonNull(client.get().uri("/Accounts/{sid}.json",sid).retrieve().body(Map.class));
  if(!"Trial".equals(conta.get("type"))) throw new IllegalStateException("Envio bloqueado: conta nao e Trial");
  var dados=new LinkedMultiValueMap<String,String>();
  dados.add("From",from);dados.add("To",to);dados.add("Body",modelo);
  client.post().uri("/Accounts/{sid}/Messages.json",sid).contentType(MediaType.APPLICATION_FORM_URLENCODED)
   .body(dados).retrieve().toBodilessEntity();
 }
}
