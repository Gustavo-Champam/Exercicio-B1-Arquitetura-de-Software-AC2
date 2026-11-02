# A3 Mock e Observabilidade

Gustavo Gutierres Champam — RA 223645  
Gustavo Oliveira Camargo — RA 236024

Finalizamos os jogos da semana anterior, salvamos o lote com os vencedores e somente após o commit tentamos SMS e e-mail. Se o banco falhar, nenhum aviso é enviado. Falhas externas são registradas na exceção final, sem impedir as tentativas dos demais canais e vencedores.

## Validação atual
`mvnw.cmd clean install`: 23 testes, 0 falhas, 0 erros, 0 ignorados. JaCoCo: 68 linhas e 12 ramos cobertos, zero perdidos. O pom exige 100% de linhas e ramos sem exclusões. A cobertura mede a aplicação refatorada, e não funcionalidades que não existem nela.

Incluímos teste de erro real no flush JPA, rollback do lote e ausência de qualquer SMS/e-mail; teste de commit antes do envio; fronteiras de data; vencedores; API MockMvc; repository; contratos HTTP Brevo/Twilio e falhas independentes de notificação.

## Executar
JDK 17. No Windows: `.\mvnw.cmd clean install`; no Linux: `sh mvnw clean install`.
Depois: `java -jar target/a3-jogos-1.0.0.jar --server.port=8083`.
Enviar `requisicao.json` por POST para `http://localhost:8083/jogos/finalizar?hoje=2026-10-26`.
O perfil padrão usa H2 e adapters de SMS/e-mail simulados, sem custos. `docker compose up -d --build` disponibiliza API 8083, Prometheus 9093 e Grafana 3003. Pare o Java manual antes de usar essas mesmas portas no Docker.

## Entrega e evidências
[Relatório atualizado](Relatorio_A3.pdf), [respostas das quatro práticas](RELATORIO.md), [testes e cobertura atuais](evidencias/validacao-atual/), [passos manuais](PASSOS_MANUAIS.md).
O laboratório Jenkins do repositório B1 executa este projeto junto com a AC1 evoluída e produz novas capturas. As imagens antigas permanecem identificadas como histórico; os relatórios atuais ficam em `evidencias/validacao-atual`.

## Provedores
Brevo e Twilio Trial possuem adapters e testes de contrato com HTTP simulado. Credenciais e recebimento real ainda dependem de configuração da conta. [Configuração](CONFIGURAR_PROVEDORES.md). Nenhuma mensagem real foi enviada nesta validação automatizada.

## Limites do laboratório
O trial Twilio aceita conteúdo predefinido e não comprova um aviso personalizado de vitória. Repetir a requisição pode reenviar avisos; não há garantia de entrega, retry durável nem pagamento financeiro real. H2 em memória perde os dados após reinício. Essas características não são simuladas como capacidades implementadas.

## Execução comprovada em 2 de novembro de 2026

[Jenkins com resultado SUCCESS](https://github.com/Gustavo-Champam/Exercicio-B1-Arquitetura-de-Software-AC2/actions/runs/41537017845). Foram 64 testes aprovados, zero falhas e zero ignorados: 41 da AC1 evoluída e 23 do A3. Ambos atingiram 100% de linhas e ramos, sem exclusões. PostgreSQL real, Docker, APIs, falha da IA, eventos, MQTT e métricas foram exercitados. Evidências e proveniência estão em evidencias/jenkins. SMS/e-mail externos não foram enviados.
