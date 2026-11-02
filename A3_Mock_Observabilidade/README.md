# SMS Twilio Trial e e-mail Brevo

A integração está implementada e testada localmente. Falta configurar suas contas e credenciais. O trial atual limita SMS a modelos predefinidos: não comprova envio real do aviso personalizado de vitória. Veja CONFIGURAR_PROVEDORES.md.

# Integração de e-mail Brevo

Consulte [CONFIGURAR_PROVEDORES.md](CONFIGURAR_PROVEDORES.md) para ativar o plano gratuito com suas credenciais locais. O build atual passou em 20 testes, com 100% de linhas e ramos. O PDF e as capturas anteriores são históricos da versão simulada. Twilio Trial está implementado no perfil sms-trial; por padrão o envio segue simulado. Nenhuma mensagem real foi enviada.

## Atualização de 2 de novembro de 2026
Implementamos SMS e e-mail após a confirmação da persistência do lote. Se o banco falhar, nenhum dos canais é chamado. O envio é sequencial: primeiro SMS, depois e-mail; falha no SMS interrompe o fluxo antes do e-mail. Não há retry automático, entrega garantida ou provedor externo configurado: SmsLog e EmailLog são adapters simulados para o estudo com mocks.

Executamos novamente clean install: 13 testes aprovados, 100% das linhas e ramos cobertos, sem exclusões. As evidências atuais estão em evidencias/build.log, evidencias/surefire e evidencias/jacoco. As capturas PNG de aplicação/Grafana/JaCoCo registram a versão de 26/10, anterior à inclusão do e-mail.

# Exercício A3 Mock e Observabilidade

Gustavo Gutierres Champam - RA 223645
Gustavo Oliveira Camargo - RA 236024

Leia RELATORIO.md ou Relatorio_A3.pdf. Projeto refatorado a partir do estudo fornecido; SMS e e-mail simulado e banco H2 em memória.

## Executar
Requisitos: JDK 17; Maven Wrapper baixa Maven na primeira execução.
```powershell
.\mvnw.cmd clean install
java -jar target/a3-jogos-1.0.0.jar --server.port=8083
```
Em outro terminal:
```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8083/jogos/finalizar?hoje=2026-10-26' -ContentType 'application/json' -InFile requisicao.json
```
Para observabilidade com Docker, pare a execução Java manual e use `docker compose up -d --build`. API 8083, Prometheus 9093, Grafana 3003. Não executar o Compose se essas portas estiverem ocupadas. Ao terminar: `docker compose down`.

## Evidências
- evidencias/build.log e surefire: JUnit e clean install.
- evidencias/jacoco/index.html e jacoco.csv: cobertura integral do código desta refatoração.
- evidencias/aplicacao.log e resposta-api.json: JAR e chamada HTTP real.
- evidencias/grafana.png, grafana-contador.png e prometheus.png: monitoramento real.

## Limites
H2 não é banco durável de produção; SmsLog não é provedor real; sem autenticação nesta API didática. Não há controle de reenvio de SMS e e-mail/idempotência. A cobertura se refere ao pacote lab, não ao ZIP original. Antes da apresentação, faremos a revisão em dupla dos testes e das decisões.
