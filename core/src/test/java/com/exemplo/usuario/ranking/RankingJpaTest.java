package com.exemplo.usuario.ranking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.EntityManager;
import com.exemplo.usuario.usuario.domain.*;
import com.exemplo.usuario.ranking.application.*;
import com.exemplo.usuario.ranking.internal.persistence.*;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest class RankingJpaTest {
 @Autowired EntityManager em;
 @Autowired RankingRepository repo;
 @Test void mesmoCalculoComJpaReal() {
  var usuario=new Usuario("Ana Silva","ana@example.com","$2a$10$hashDidaticoNaoUsarEmProducao");
  var assinatura=new Assinatura(usuario);
  assinatura.setCursosConcluidosComSucesso(3);assinatura.setMoedas(20);
  usuario.vincularAssinatura(assinatura);em.persist(usuario);em.flush();em.clear();
  assertEquals(320,new RankingService(new RankingJpaAdapter(repo)).listar().get(0).getPontos());
 }
}
