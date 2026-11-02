package lab;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service public class PersistenciaJpa implements SalvarLote {
 private final JogosRepository repository;
 public PersistenciaJpa(JogosRepository repository) { this.repository=repository; }
 // O proxy transacional termina o commit ANTES de retornar ao chamador.
 @Transactional public void salvar(List<Finalizado> jogos) { repository.saveAllAndFlush(jogos); }
}
