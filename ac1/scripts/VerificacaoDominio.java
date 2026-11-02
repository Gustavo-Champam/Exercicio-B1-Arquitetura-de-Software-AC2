import br.com.educacao.gamificada.domain.Aluno;
import br.com.educacao.gamificada.domain.Plano;

/** Verificação auxiliar com Java 17. Não substitui JUnit, JaCoCo ou IntelliJ. */
public class VerificacaoDominio {
    private static int aprovados;
    private static int falhas;

    public static void main(String[] argumentos) {
        verificar("12o curso com media 7 promove", () -> {
            var aluno = new Aluno("Gustavo", Plano.BASICO, 11);
            exigir(aluno.concluirCurso(7.0));
            exigir(aluno.getPlano() == Plano.PREMIUM && aluno.getCursosConcluidos() == 12);
        });
        verificar("11o curso mantem Basico", () -> {
            var aluno = new Aluno("Gustavo", Plano.BASICO, 10);
            exigir(!aluno.concluirCurso(10.0));
            exigir(aluno.getPlano() == Plano.BASICO && aluno.getCursosConcluidos() == 11);
        });
        for (double media : new double[]{0, 6.99}) {
            verificar("media " + media + " nao conta nem promove", () -> {
                var aluno = new Aluno("Gustavo", Plano.BASICO, 11);
                exigir(!aluno.concluirCurso(media));
                exigir(aluno.getPlano() == Plano.BASICO && aluno.getCursosConcluidos() == 11);
            });
        }
        verificar("nao repete promocao", () -> {
            var aluno = new Aluno("Gustavo", Plano.PREMIUM, 12);
            exigir(!aluno.concluirCurso(8.0));
            exigir(aluno.getPlano() == Plano.PREMIUM && aluno.getCursosConcluidos() == 13);
        });
        verificar("consulta dados iniciais", () -> {
            var aluno = new Aluno("Gustavo", Plano.BASICO, 0);
            exigir(aluno.getNome().equals("Gustavo"));
            exigir(aluno.getPlano() == Plano.BASICO && aluno.getCursosConcluidos() == 0);
        });
        for (double media : new double[]{-0.01, 10.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            verificar("rejeita media " + media, () -> {
                var aluno = new Aluno("Gustavo", Plano.BASICO, 11);
                rejeitar(() -> aluno.concluirCurso(media));
                exigir(aluno.getPlano() == Plano.BASICO && aluno.getCursosConcluidos() == 11);
            });
        }
        verificar("rejeita nome nulo", () -> rejeitar(() -> new Aluno(null, Plano.BASICO, 0)));
        verificar("rejeita nome vazio", () -> rejeitar(() -> new Aluno(" ", Plano.BASICO, 0)));
        verificar("rejeita plano nulo", () -> rejeitar(() -> new Aluno("Gustavo", null, 0)));
        verificar("rejeita contagem negativa", () -> rejeitar(() -> new Aluno("Gustavo", Plano.BASICO, -1)));
        System.out.printf("RESULTADO: %d passaram; %d falharam.%n", aprovados, falhas);
        if (falhas > 0) System.exit(1);
    }

    private static void verificar(String nome, Runnable teste) {
        try { teste.run(); aprovados++; System.out.println("PASS: " + nome); }
        catch (AssertionError | RuntimeException erro) {
            falhas++;
            System.out.println("FAIL: " + nome + " -- " + erro.getClass().getSimpleName());
        }
    }

    private static void exigir(boolean condicao) {
        if (!condicao) throw new AssertionError("Resultado diferente do esperado");
    }

    private static void rejeitar(Runnable operacao) {
        try { operacao.run(); }
        catch (IllegalArgumentException esperado) { return; }
        throw new AssertionError("Era esperada IllegalArgumentException");
    }
}
