package com.exemplo.usuario.ia;
import org.junit.jupiter.api.Test;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import static org.junit.jupiter.api.Assertions.*;
class IaTest {
 @Test void quedaDaIaAtivaFallbackSemBloquear() {
  var metrics=new SimpleMeterRegistry();
  assertTrue(new RecomendacaoClient("http://127.0.0.1:1",metrics).recomendar().contains("catalogo"));
  assertEquals(1,metrics.counter("ia.fallback").count());
 }
}
