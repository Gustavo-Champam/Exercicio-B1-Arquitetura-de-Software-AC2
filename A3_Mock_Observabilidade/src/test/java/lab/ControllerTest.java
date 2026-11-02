package lab;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(JogosController.class) @Import(SimpleMeterRegistry.class) class ControllerTest {
 @Autowired MockMvc mvc; @MockBean FinalizaJogo servico;
 @Test void apiComStubEVerify() throws Exception {
  // Stub controla retorno; verify comprova delegacao. DispatcherServlet e JSON sao reais.
  when(servico.finalizar(anyList(),any())).thenReturn(2);
  mvc.perform(post("/jogos/finalizar?hoje=2026-10-26").contentType("application/json").content("[]"))
   .andExpect(status().isOk()).andExpect(content().string("2"));
  verify(servico).finalizar(java.util.List.of(),java.time.LocalDate.of(2026,10,26));
 }
 @Test void rejeitaDataInvalida() throws Exception {
  mvc.perform(post("/jogos/finalizar?hoje=abc").contentType("application/json").content("[]")).andExpect(status().isBadRequest());
  verifyNoInteractions(servico);
 }
}
