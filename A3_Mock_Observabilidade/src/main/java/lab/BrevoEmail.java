package lab;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
@Component @Profile("email-real")
public class BrevoEmail implements Email {
 private final RestClient client;
 private final String remetente;
 private final Map<String,String> destinatarios;
 public BrevoEmail(RestClient.Builder builder, @Value("${BREVO_API_KEY}") String chave,
  @Value("${BREVO_REMETENTE}") String remetente, @Value("${BREVO_DESTINATARIOS}") String contatos) throws Exception {
  this.client=builder.baseUrl("https://api.brevo.com/v3").defaultHeader("api-key",chave).build();
  this.remetente=remetente;
  this.destinatarios=new ObjectMapper().readValue(contatos,new TypeReference<Map<String,String>>(){});
 }
 public void enviar(Finalizado jogo) {
  String destino=Objects.requireNonNull(destinatarios.get(jogo.getVencedor()),"Vencedor sem email cadastrado");
  client.post().uri("/smtp/email").contentType(MediaType.APPLICATION_JSON).body(Map.of(
   "sender",Map.of("email",remetente),"to",List.of(Map.of("email",destino)),
   "subject","Resultado do jogo "+jogo.getId(),
   "textContent","Parabens, "+jogo.getVencedor()+"! Voce venceu o jogo "+jogo.getId()+"."))
   .retrieve().toBodilessEntity();
 }
}
