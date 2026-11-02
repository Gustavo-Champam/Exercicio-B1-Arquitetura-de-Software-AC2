package br.com.educacao.gamificada;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("test")
class EducacaoGamificadaApplicationTests {

    @Test
    void contextLoads() {
    }
}
