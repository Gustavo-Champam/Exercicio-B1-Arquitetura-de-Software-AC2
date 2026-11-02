# Exercício B1 Arquitetura de Software

Gustavo Gutierres Champam - RA 223645
Gustavo Oliveira Camargo - RA 236024

Registro: [docs/arquitetura/README.md](docs/arquitetura/README.md). Relatório: Relatorio_B1.pdf. Visões: docs/arquitetura/c4.md. Síntese AF: docs/arquitetura/AF.md.

## Executar Core e IA
Requisitos: JDK 17, Python 3.12, acesso à internet para dependências. No Windows, usar mvnw.cmd; no Linux, ./mvnw.
```powershell
cd core
.\mvnw.cmd clean verify
java -jar target/b1-monolito-modular-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2 --server.port=8084
```
Em outro terminal, na raiz B1:
```powershell
python -m venv .venv
.\.venv\Scripts\python -m pip install -r ia/requirements.txt -r microsolucoes/requirements.txt
.\.venv\Scripts\python -m uvicorn main:app --app-dir ia --port 8001
```
GET http://localhost:8084/api/recomendacoes com Basic Auth didático admin/123456. Com IA ligada, origem python. Interromper uvicorn e repetir: origem catalogo. Métrica /actuator/prometheus: ia_fallback_total.

## Microsoluções
```powershell
.\.venv\Scripts\python microsolucoes/eventos.py
.\.venv\Scripts\python microsolucoes/mqtt_lab.py
```
O broker MQTT usa somente loopback 18883 e termina após o experimento. O lab de eventos usa banco temporário e testa recuperação/repetição. Ambos demonstram hipóteses, ainda sem integração com matrícula/Core.

## Operação
`docker compose up -d --build` após build do Core. API 8084, Prometheus 9094, Grafana 3004. O dashboard e datasource são provisionados. `docker compose stop ia` provoca fallback. Terminar com `docker compose down`. Esse Compose não foi executado localmente; as capturas foram produzidas com processos nativos, usando Prometheus 9093 e Grafana 3003 compartilhados com o lab A3.

Jenkinsfile: agente Linux, tool jdk17, Docker/Compose e Python. Testes precedem build e deploy. Ainda requer execução no Jenkins da equipe. A2 não está incluído.

## Repositório e entrega
Repositório da equipe: https://github.com/Gustavo-Champam/Exerc-cio-B1---Arquitetura-de-Software-AC2

O relatório e as evidências registram os experimentos de 26/10/2026. A publicação foi preparada em 02/11/2026. A execução no Jenkins e a integração ao MVP da AC1 ainda precisam ser verificadas. O A3 está na pasta A3_Mock_Observabilidade, com código, testes e instruções de configuração dos provedores.

## Referências
Enunciado Exercicio_B1_Architecture_Lab_6_Desafios_Reavaliado.docx; exemplos de monólito em camadas/modular; guia FURPS+ e templates C4/ADR fornecidos. Conceitos C4: https://c4model.com/ ; transações Spring: https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html


## A3 Mock e Observabilidade

[Projeto A3](A3_Mock_Observabilidade/README.md) | [Configurar Brevo e Twilio Trial](A3_Mock_Observabilidade/CONFIGURAR_PROVEDORES.md)

Implementamos SMS e e-mail após persistência, adapters Brevo e Twilio Trial e testes sem envios externos. O build passou em 20 testes com 100% de cobertura de linhas e ramos. O uso real exige credenciais locais; o trial Twilio aceita apenas conteúdo predefinido. Nenhuma credencial real é versionada. Relatórios PDF e capturas anteriores são históricos; os logs e o JaCoCo HTML registram o build atual.
