package com.exemplo.usuario.ia;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
@Service public class RecomendacaoClient {
 private final String url; private final MeterRegistry metrics;
 public RecomendacaoClient(@Value("${ia.url:http://localhost:8001}") String url,MeterRegistry metrics) {this.url=url;this.metrics=metrics;}
 public String recomendar() {
  try {
   var req=HttpRequest.newBuilder(URI.create(url+"/recomendacoes")).timeout(Duration.ofMillis(800)).GET().build();
   var response=HttpClient.newBuilder().connectTimeout(Duration.ofMillis(300)).build().send(req,HttpResponse.BodyHandlers.ofString());
   if(response.statusCode()!=200) throw new java.io.IOException("IA HTTP "+response.statusCode());
   return response.body();
  } catch(InterruptedException e) {Thread.currentThread().interrupt();metrics.counter("ia.fallback").increment();return "{\"origem\":\"catalogo\",\"cursos\":[\"Introducao a Java\"]}";
  } catch(java.io.IOException e) {metrics.counter("ia.fallback").increment();return "{\"origem\":\"catalogo\",\"cursos\":[\"Introducao a Java\"]}";}
 }
}
