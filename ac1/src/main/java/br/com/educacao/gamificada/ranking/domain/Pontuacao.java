package br.com.educacao.gamificada.ranking.domain;
public final class Pontuacao {
 private Pontuacao() {}
 public static int calcular(int cursos,int moedas) {
  if(cursos<0 || moedas<0) throw new IllegalArgumentException("Valores negativos");
  return Math.addExact(Math.multiplyExact(cursos,100),moedas);
 }
}