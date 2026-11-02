package br.com.educacao.gamificada.ia;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.MockRestServiceServer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.http.MediaType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;
class RecomendacaoTest {
 @Test void sucessoEFalhaComContador() throws Exception {
  var builder=RestClient.builder();var metrics=new SimpleMeterRegistry();
  var server=com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1",0),0);
  var status=new java.util.concurrent.atomic.AtomicInteger(200);
  server.createContext("/recomendacoes",e->{byte[] body="{\"origem\":\"python\"}".getBytes();e.sendResponseHeaders(status.get(),body.length);e.getResponseBody().write(body);e.close();});
  server.start();
  try {
   var client=new RecomendacaoClient(builder,"http://127.0.0.1:"+server.getAddress().getPort(),metrics);
   var controller=new IaController(client);
   assertTrue(controller.recomendar().contains("python"));
   status.set(503);assertTrue(controller.recomendar().contains("catalogo"));
   assertEquals(1,metrics.counter("ia.fallback").count());
   server.stop(0);assertTrue(controller.recomendar().contains("catalogo"));
   assertEquals(2,metrics.counter("ia.fallback").count());
  } finally {server.stop(0);}
 }
}
