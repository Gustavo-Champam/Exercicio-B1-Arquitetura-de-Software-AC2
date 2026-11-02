# Conferência dos requisitos do B1

Fonte da AC1 rastreada em origem_ac1.json; execução real: https://github.com/Gustavo-Champam/Exercicio-B1-Arquitetura-de-Software-AC2/actions/runs/41537017845.

| Desafio | Evidência de atendimento |
|---|---|
| 1 Drivers e evolução | RF/RNF, três ASRs, RPC, três alternativas, ADR-001, Container e modularização da recompensa da AC1 com antes/depois |
| 2 Organização interna | Porta RankingPort, regra pura, adapter fake/JPA, teste de 1203 pontos após a promoção real, ADR-002 e Component |
| 3 Python e IA | Java da AC1 consome FastAPI; container interrompido; fallback e contador comprovados; ADR-003 e Container |
| 4 Eventos | REST falha, outbox persiste e recupera após reabrir banco; deduplicação; ADR-004 e sequência |
| 5 IoT | Broker MQTT real, QoS 1, duas mensagens e um efeito; gateway transforma evento; ADR-005 e Container |
| 6 Operação | Jenkins executou builds, testes, Docker, APIs, Prometheus e Grafana; ADR-006 e Deployment |

Cada ADR contém alternativas e consequências; a matriz cumulativa contém no máximo três ASRs por desafio. Eventos e MQTT atendem ao escopo dos mini-labs; integração produtiva, RAG completo, alta carga, TLS/ACL e rollback são evolução da AF. Não apresentamos esses itens como já concluídos.
