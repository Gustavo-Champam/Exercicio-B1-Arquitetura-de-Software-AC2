package lab;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest class RepositoryTest {
 @Autowired JogosRepository repo; @Autowired EntityManager em;
 @Test void gravaERecarregaVencedorDoBanco() {
  repo.saveAndFlush(new Finalizado(7,"Ana")); em.clear();
  Finalizado salvo=repo.findById(7L).orElseThrow();
  assertEquals(7L,salvo.getId()); assertEquals("Ana",salvo.getVencedor());
 }
}
