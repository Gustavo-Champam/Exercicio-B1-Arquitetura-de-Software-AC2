# B1 Arquitetura de Software

Gustavo Gutierres Champam — RA 223645  
Gustavo Oliveira Camargo — RA 236024

## Entrega
[Respostas e decisões](docs/arquitetura/README.md), [ADRs](docs/adr), [C4](docs/arquitetura/c4.md), [matriz ASR](docs/arquitetura/matriz_asr.csv), [síntese AF](docs/arquitetura/AF.md).

## Projeto da equipe
`ac1/` contém a evolução do MVP real da equipe, preservando as regras e testes de promoção/recompensas. `core/` é a referência inicial para comparação. A implantação usa ac1, com ranking, IA e métricas. [Origem](docs/arquitetura/origem_ac1.json) e [antes/depois](docs/arquitetura/comparacao_ac1.json).

## Executar
Java 17. Dentro de ac1: `mvnw.cmd clean verify` e `java -jar target/educacao-gamificada-0.0.1-SNAPSHOT.jar --server.port=8084`.
Na raiz, instalar dependências Python de ia/requirements.txt e microsolucoes/requirements.txt. Executar `python -m uvicorn main:app --app-dir ia --port 8001`.
GET /api/ranking consulta os alunos reais; GET /api/recomendacoes usa FastAPI e devolve catálogo quando a IA está indisponível. POST /api/alunos e /api/alunos/{id}/conclusoes preservam a AC1.

## Jenkins e observabilidade
O workflow .github/workflows/laboratorio.yml prepara um runner temporário com Jenkins. Jenkins executa o Jenkinsfile: PostgreSQL real, testes e JaCoCo AC1/A3, eventos/MQTT, build/deploy Docker e validação de APIs/fallback/métricas. As evidências são publicadas como artifact evidencias-b1-a3. A execução concreta e seu resultado constam na pasta evidencias/jenkins quando o artifact é recolhido.
Para reproduzir a stack após compilar: `docker compose up -d --build`. AC1 8084; Prometheus 9094; Grafana 3004. `docker compose stop ia` provoca fallback. O contador ia_fallback_total distingue degradação de simples HTTP 200.

## Microsoluções e limites
`python microsolucoes/eventos.py` testa REST com falha, outbox durável e reentrega. `python microsolucoes/mqtt_lab.py` executa broker real em loopback, QoS 1 e deduplicação. Integração de ambos no fluxo produtivo, carga de 100 mil usuários, RAG completo, TLS/ACL e rollback são próximos passos da AF, não resultados alegados.

## A3
[Projeto A3](A3_Mock_Observabilidade/README.md) | [Passos manuais dos provedores](A3_Mock_Observabilidade/PASSOS_MANUAIS.md).
Repositório: https://github.com/Gustavo-Champam/Exercicio-B1-Arquitetura-de-Software-AC2 . A2 e AC1 originais não foram alterados.

## Execução comprovada em 2 de novembro de 2026

[Jenkins com resultado SUCCESS](https://github.com/Gustavo-Champam/Exercicio-B1-Arquitetura-de-Software-AC2/actions/runs/41537017845). Foram 64 testes aprovados, zero falhas e zero ignorados: 41 da AC1 evoluída e 23 do A3. Ambos atingiram 100% de linhas e ramos, sem exclusões. PostgreSQL real, Docker, APIs, falha da IA, eventos, MQTT e métricas foram exercitados. Evidências e proveniência estão em evidencias/jenkins. SMS/e-mail externos não foram enviados.
