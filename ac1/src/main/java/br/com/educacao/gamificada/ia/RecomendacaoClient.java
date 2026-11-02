package br.com.educacao.gamificada.ia;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import java.net.http.HttpClient;
import java.time.Duration;
@Service public class RecomendacaoClient {
 private final RestClient client; private final MeterRegistry metrics;
 public RecomendacaoClient(RestClient.Builder builder,@Value("${ia.url:http://localhost:8001}") String url,MeterRegistry metrics){
  var factory=new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofMillis(300)).build());
  factory.setReadTimeout(Duration.ofMillis(800));
  this.client=builder.requestFactory(factory).baseUrl(url).build();this.metrics=metrics;
 }
 public String recomendar(){
  try {return client.get().uri("/recomendacoes").retrieve().body(String.class);}
  catch(RestClientException e){metrics.counter("ia.fallback").increment();return "{\"origem\":\"catalogo\",\"cursos\":[\"Introducao a Java\"]}";}
 }
}