# US01 — Progressão do Aluno Básico

**Responsável pela US01 e pelos BDD01 a BDD07: Gustavo Camargo.**

Fonte: aba `pb`, linha 5 de `Template_ATDD.xlsx`, README original e escopo confirmado pelo responsável: sua parte termina na alteração de Básico para Premium.

## História

Como aluno ativo da plataforma com assinatura básica, quero que minha assinatura seja atualizada automaticamente para Premium ao atingir 12 cursos concluídos com média válida, para ter acesso aos benefícios do novo plano.

## Critérios adotados

- Aluno novo começa Básico com zero cursos.
- Contabilizar cursos distintos com média entre 7,0 e 10,0, inclusive.
- Nota abaixo de 7,0 mantém contador/plano e permite nova tentativa do curso.
- Ao passar de 11 para 12 cursos válidos, alterar para Premium e publicar um evento interno.
- Não duplicar a contagem do mesmo curso nem repetir a promoção.
- Cursos em andamento não devem ser enviados ao endpoint de conclusão; acompanhamento do andamento está fora da US.

A interpretação de que todos os cursos contabilizados precisam de média válida explicita uma condição detalhada pela planilha apenas para o 12º curso. Confirmar essa interpretação na validação acadêmica.

## Cenários e rastreabilidade

| ID | Dado / E | Quando | Então / E | Testes automatizados e executados |
| --- | --- | --- | --- | --- |
| BDD01 | Básico com 11 cursos válidos | Conclui outro com média 7,0 | Premium, 12 cursos e evento de promoção | `devePromoverAlunoAoConcluirDecimoSegundoCursoComNotaValida`; `devePersistirPromocaoNoDecimoSegundoCursoEEmitirUmEvento` |
| BDD02 | Básico com 11 cursos válidos | Conclui outro com média 6,99 | Básico, 11 cursos, sem evento | `naoDeveContabilizarCursoNemPromoverAlunoComNotaInferiorASete`; `naoDeveContarNotaBaixaEDevePermitirNovaConclusaoDoMesmoCurso` |
| BDD03 | Básico com 10 cursos válidos | Conclui o 11º com média 10,0 | Básico com 11 cursos | `naoDevePromoverAlunoAntesDeDozeCursos` |
| BDD04 | Curso já contabilizado | Reenvia o mesmo código | HTTP 409; contador não aumenta | `deveImpedirDuplicidadeMesmoComEspacosOuLetrasMinusculas` |
| BDD05 | Aluno já promovido | Conclui outro curso válido | Mantém Premium; sem novo evento | `naoDevePromoverNovamenteAlunoQueJaEhPremium`; `naoDeveReemitirEventoAposPromocao` |
| BDD06 | Aluno com dados válidos | Recebe média fora de 0–10 ou não finita | Rejeita sem alterar estado | `deveRejeitarMediaInvalidaSemAlterarAluno`; `deveRejeitarConclusaoInvalidaSemPersistir` |
| BDD07 | Básico com 11 cursos no PostgreSQL | Conclui o 12º válido | Consulta ao banco retorna Premium e 12 cursos | `devePersistirPromocaoEmPostgresReal` |

## Exemplo Gherkin — documentação

```gherkin
# language: pt
Funcionalidade: Promoção do Aluno Básico
  Cenário: Promover ao concluir o décimo segundo curso com a nota mínima
    Dado que o aluno possui uma assinatura Básica
    E possui 11 cursos válidos distintos concluídos
    Quando concluir um novo curso com média 7,0
    Então seu plano deve ser alterado para Premium
    E o contador deve ser 12
    E deve ser publicado um evento de promoção

  Cenário: Não promover com média inferior a sete
    Dado que o aluno possui uma assinatura Básica
    E possui 11 cursos válidos distintos concluídos
    Quando concluir um novo curso com média 6,99
    Então seu plano deve permanecer Básico
    E o contador deve permanecer 11
    E não deve ser publicado evento de promoção
```

Os cenários são implementados com JUnit 5. Cucumber é opcional e não foi adicionado.

## Integração com a US02

`AlunoPromovidoEvent(Long alunoId)` é interno ao Spring e publicado na transação de promoção. Na entrega integrada, `RecompensaService` recebe o evento com `@EventListener` e participa da mesma transação para registrar a origem dos prêmios atomicamente com a promoção. Um erro reverte toda a operação. A chave por aluno e o bloqueio de escrita impedem reprocessamento duplicado.

A responsabilidade acadêmica da US01 continua sendo a progressão. A US02 complementa o fluxo com voucher, moedas e histórico. O evento é interno e síncrono, sem fila externa.
