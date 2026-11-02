package lab;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
@org.springframework.context.annotation.Profile("!email-real")
@Component public class EmailLog implements Email {
 public void enviar(Finalizado jogo) { LoggerFactory.getLogger(EmailLog.class).info("Email simulado jogo={} vencedor={}",jogo.getId(),jogo.getVencedor()); }
}
