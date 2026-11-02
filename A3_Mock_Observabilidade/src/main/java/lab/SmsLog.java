package lab;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
@org.springframework.context.annotation.Profile("!sms-trial")
@Component public class SmsLog implements Sms {
 // Adapter didatico: nao envia mensagens reais nem exige telefone ou credenciais.
 public void enviar(Finalizado jogo) { LoggerFactory.getLogger(SmsLog.class).info("SMS simulado jogo={} vencedor={}",jogo.getId(),jogo.getVencedor()); }
}
