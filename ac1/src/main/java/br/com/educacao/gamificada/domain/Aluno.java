package br.com.educacao.gamificada.domain;

public class Aluno {

    private static final int VOUCHERS_PREMIUM = 1;
    private static final int MOEDAS_PREMIUM = 3;
    private static final double MEDIA_MINIMA = 7.0;
    private static final int CURSOS_PARA_PREMIUM = 12;

    private final String nome;
    private Plano plano;
    private int cursosConcluidos;
    private int vouchers;
    private int moedas;

    public Aluno(String nome, Plano plano, int cursosConcluidos) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (plano == null) {
            throw new IllegalArgumentException("O plano é obrigatório.");
        }
        if (cursosConcluidos < 0) {
            throw new IllegalArgumentException("A quantidade de cursos não pode ser negativa.");
        }
        this.nome = nome;
        this.plano = plano;
        this.cursosConcluidos = cursosConcluidos;
        concederRecompensasPremium();
    }

    public boolean concluirCurso(double media) {
        validarMedia(media);
        if (media < MEDIA_MINIMA) {
            return false;
        }

        cursosConcluidos++;
        if (podeSerPromovido()) {
            plano = Plano.PREMIUM;
            concederRecompensasPremium();
            return true;
        }
        return false;
    }

    private static void validarMedia(double media) {
        if (!Double.isFinite(media) || media < 0 || media > 10) {
            throw new IllegalArgumentException("A média deve estar entre 0 e 10.");
        }
    }

    private boolean podeSerPromovido() {
        return plano == Plano.BASICO && cursosConcluidos >= CURSOS_PARA_PREMIUM;
    }

    public int getVouchers() { return vouchers; }
    public int getMoedas() { return moedas; }
    public void concederRecompensasPremium() {
        if (plano == Plano.PREMIUM) {
            vouchers = VOUCHERS_PREMIUM;
            moedas = MOEDAS_PREMIUM;
        }
    }

    public String getNome() { return nome; }
    public Plano getPlano() { return plano; }
    public int getCursosConcluidos() { return cursosConcluidos; }
}
