# US02 — Recompensas Premium

**Responsável pela US e pelos BDD: Gustavo Champam.**

Como aluno recém-promovido ao Premium, quero receber 1 voucher de projeto real e 3 moedas virtuais na carteira, para acessar as recompensas da plataforma.

## BDD 1 — Receber voucher e moedas

Dado um aluno Básico com 11 cursos válidos, quando concluir o 12º curso com média 7,0, então será Premium e receberá 1 voucher e 3 moedas.

Automação: `AlunoPremiumTest.deveConcederRecompensasNaPromocaoEManterAposOutroCurso` e `AlunoControllerTest.bddUS02DevePersistirUmaRecompensaMesmoAposNovoCursoEDuplicata`.

## BDD 2 — Registrar origem

Dado um aluno promovido, quando o sistema receber `AlunoPromovidoEvent`, então gravará no histórico o aluno, 1 voucher, 3 moedas, a origem `PROMOCAO_PREMIUM` e a data da concessão. A carteira é habilitada pela promoção.

Automação: `RecompensaAceitacaoTest.bddUS02HistoricoRegistraOrigemDosDepositosUmaUnicaVez`.

## BDD 3 — Não duplicar

Dado um aluno que já recebeu os prêmios, quando o evento for reprocessado duas vezes ou outro curso for concluído, então continuará com apenas 1 voucher, 3 moedas e um registro no histórico. A repetição do mesmo curso aprovado retorna HTTP 409.

Automação: `AlunoPremiumTest.naoDeveReceberRecompensaDuasVezes`, `RecompensaAceitacaoTest` e `AlunoControllerTest`.

## BDD 4 — Básico sem recompensa

Dado um aluno Básico, quando tentar receber recompensa diretamente ou concluir com média 6,99, então permanecerá com zero vouchers, zero moedas e histórico vazio.

Automação: `AlunoPremiumTest.basicoNaoRecebeRecompensasNemComSolicitacaoDireta` e `RecompensaAceitacaoTest`.

Os cenários da planilha são implementados por JUnit/MockMvc. Cucumber não é utilizado. Os critérios foram verificados em H2 e os saldos após promoção também em PostgreSQL real.
