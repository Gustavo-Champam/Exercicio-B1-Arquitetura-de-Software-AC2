package br.com.educacao.gamificada;

import br.com.educacao.gamificada.domain.Plano;
import br.com.educacao.gamificada.dto.ConcluirCursoRequest;
import br.com.educacao.gamificada.dto.CriarAlunoRequest;
import br.com.educacao.gamificada.service.AlunoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("postgres")
@EnabledIfEnvironmentVariable(named = "TESTAR_POSTGRES", matches = "true")
@Transactional
class AlunoPostgresTest {
    @Autowired DataSource banco;
    @Autowired AlunoService servico;
    @Autowired EntityManager persistencia;

    @Test
    void devePersistirPromocaoEmPostgresReal() throws Exception {
        try (var conexao = banco.getConnection()) {
            assertEquals("PostgreSQL", conexao.getMetaData().getDatabaseProductName());
        }
        var aluno = servico.criar(new CriarAlunoRequest("Aluno de teste PostgreSQL"));
        for (int numero = 1; numero <= 12; numero++) {
            servico.concluirCurso(aluno.id(), new ConcluirCursoRequest("CURSO-" + numero, 7.0));
        }
        persistencia.flush();
        persistencia.clear();
        var salvo = servico.buscar(aluno.id());
        assertEquals(Plano.PREMIUM, salvo.plano());
        assertEquals(12, salvo.cursosConcluidos());
        assertEquals(1, salvo.vouchers());
        assertEquals(3, salvo.moedas());
        servico.concluirCurso(aluno.id(), new ConcluirCursoRequest("CURSO-13", 9.0));
        persistencia.flush();
        persistencia.clear();
        assertEquals(1, servico.buscar(aluno.id()).vouchers());
        assertEquals(3, servico.buscar(aluno.id()).moedas());
        // A transação do teste é revertida ao terminar.
    }
}
