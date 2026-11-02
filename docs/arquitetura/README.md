# B1 Arquitetura de Software

Gustavo Gutierres Champam — RA 223645  
Gustavo Oliveira Camargo — RA 236024

Evoluímos o MVP real da AC1 em ac1/. Mantivemos promoção, recompensas e testes; organizamos o módulo de recompensas e adicionamos ranking com Ports & Adapters e integração FastAPI com fallback. A decisão macro continua sendo monólito modular. Eventos e MQTT são microsoluções separadas, conforme os mini-labs do enunciado; integração completa e recursos de produção ficam para a AF.

Fonte: https://github.com/Gustavo-Champam/educacao-continuada-gamificada. Commit e rastreabilidade em origem_ac1.json. A pasta core/ registra a primeira comparação com o projeto de referência e não é mais o Core implantado.

[Matriz de ASRs](matriz_asr.csv) | [C4](c4.md) | [Síntese AF](AF.md) | [Comparação da AC1](comparacao_ac1.json)

# ADR-001 Drivers e evolução do Core

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
RF01 Ranking e RF02 Conquistas. O cenário de crescimento para até 100 mil usuários representa uma previsão de capacidade, não uma medição de simultaneidade. RNFs: manutenibilidade (S), desempenho e escalabilidade (P), testabilidade (S) e complexidade operacional (S). Testabilidade é importante, mas os três ASRs selecionados abaixo são os que dirigem a escolha macro.

## ASRs prioritários
D1-A1 - Manutenibilidade (S): Localizar mudanças no módulo de ranking, restringindo acesso à persistência. Verificação: Domínio de pontuação sem imports de Spring/JPA; revisão de dependências.
D1-A2 - Escalabilidade (P): Preservar fronteira para extrair ranking apenas se a carga justificar outro deploy. Verificação: Meta proposta: p95 de leitura < 300 ms sob carga definida na AF; não validada para 100 mil usuários.
D1-A3 - Complexidade operacional (S): Manter um deploy e um banco para equipe de seis pessoas. Verificação: Contagem dos processos essenciais do Core: um.

## RPCs
Java/Spring Boot, banco relacional, legado da AC1 e equipe de seis desenvolvedores. A evolução agora parte do repositório educacao-continuada-gamificada da equipe, commit registrado em origem_ac1.json. O código executável está em ac1; core preserva somente a comparação inicial com a referência.

## Alternativas e trade offs
A - Camadas: operação simples, mas mudanças em uma capacidade atravessam pastas horizontais. B - Monólito modular: agrupa capacidade e explicita limites mantendo uma transação local; requer disciplina de dependências. C - Extração seletiva de ranking: escala e deploy próprios, mas adiciona contrato remoto, consistência eventual e operação. O volume total de usuários, isoladamente, não prova a necessidade de C.

## Decisão
Escolhemos o monólito modular e mantivemos o ranking no mesmo processo. O critério de revisão é saturação comprovada do ranking, apesar de paginação, índices e otimização, ou necessidade frequente de deploy independente. A equipe de seis pessoas favorece reduzir componentes operacionais enquanto não há essa evidência.

## Consequências
Um único deploy continua sendo ponto comum de falha e escala. O adapter de ranking da AC1 ainda lê todos os alunos; a modularização não elimina esse custo nem comprova capacidade para 100 mil usuários. Paginação e projeção de leitura são próximos experimentos. O módulo de recompensas mantém relação com AlunoEntity e evento AlunoPromovidoEvent, dependências explícitas que ainda exigem disciplina.

## Evidência
origem_ac1.json e comparacao_ac1.json registram a base real e as dependências. Movemos RecompensaService, RecompensaRepository, RecompensaEntity e RecompensaResponse para o módulo recompensa. O novo ranking usa dados reais de aluno, cursos e moedas, com porta, domínio puro e adapter JPA. Os testes originais de promoção e recompensas foram preservados.

## Impacto C4
Container: navegador, Core e banco permanecem. Component: ranking passa a ter API, serviço, porta, regra de pontuação e adapter JPA.


# ADR-002 Organização interna do Core

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
RF01 Ranking e RF02 regras de gamificação da AC1, incluindo promoção após 12 cursos aprovados, um voucher e três moedas. RNFs: manutenibilidade, modificabilidade, testabilidade e evolução (S). Selecionamos modificabilidade e testabilidade como ASRs; não é preciso criar um terceiro apenas para preencher a matriz.

## ASRs prioritários
D2-A1 - Modificabilidade (S): Inverter a dependência da aplicação para uma porta de leitura. Verificação: RankingService depende de RankingPort, não do repository.
D2-A2 - Testabilidade (S): Executar regra e caso de uso sem inicializar Spring nem banco. Verificação: Pontuacao e fake testados com JUnit; mesmo resultado com JPA/H2.

## RPCs
Spring Boot permanece; regras existentes devem ser reaproveitadas; não reescrever o produto inteiro. Preservamos as regras originais de promoção e recompensas. Adicionamos ranking com fórmula cursos concluídos × 100 + moedas e desempate por ID.

## Alternativas e trade offs
A - Serviço diretamente ligado a AlunoRepository e entidade AlunoEntity: menos tipos, mas testes e mudanças de banco afetam a aplicação. B - Porta de leitura RankingPort e adapter JPA: a aplicação recebe dados simples, e o domínio calcula os pontos; adiciona mapeamento e uma interface. DDD ajuda a nomear capacidades e regras; Hexagonal define portas/adapters; Clean e Onion descrevem direção de dependências. Não são produtos obrigatórios nem padrões mutuamente exclusivos.

## Decisão
Aplicamos Ports & Adapters somente ao ranking. RankingPort pertence à aplicação; RankingJpaAdapter implementa a porta; Pontuacao é Java puro. A aplicação usa @Service e a transação de leitura pertence ao adapter JPA: é uma adoção incremental de Ports & Adapters.

## Consequências
Trocar persistência exige implementar a porta, sem mudar a regra. Existem mais tipos e mapeamentos. A ordenação é feita em memória pelo resultado calculado no domínio. Paginação e projeção de leitura serão avaliadas após medir carga.

## Evidência
RankingTest valida pontuação e ordenação com porta fake. RankingIntegrationTest usa AlunoService real: conclui 12 cursos da AC1, promove o aluno e verifica 1203 pontos no adapter JPA. Pontuacao testa negativos e overflow. A regra não importa Spring ou JPA.

## Impacto C4
Component do Core mostra controller → serviço → porta; adapter JPA implementa porta e acessa repository; serviço chama Pontuacao.


# ADR-003 Serviço Python e IA

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
RF03 recomendação e RF04 assistente educacional. RNFs: deploy independente, escalabilidade, tolerância a falhas, interoperabilidade, desempenho e manutenibilidade. Interoperabilidade HTTP/JSON é necessária, mas os ASRs prioritários são autonomia de deploy, falha e escala.

## ASRs prioritários
D3-A1 - Deploy independente (S): Separar o processo Python do Core para publicar modelos sem recompilar Java. Verificação: Dockerfiles separados; endpoint FastAPI executado em processo distinto.
D3-A2 - Tolerância a falhas (R): Limitar espera da IA e responder com catálogo quando ela falhar. Verificação: HTTP 200 com origem python e, após queda, origem catalogo.
D3-A3 - Escalabilidade (P): Permitir ampliar réplicas da IA sem replicar todo o Core. Verificação: Fronteira de processo presente; teste de carga ainda necessário.

## RPCs
Core em Java/Spring Boot; ML e RAG em Python; a indisponibilidade da IA não pode bloquear operações essenciais. O mini-lab usa uma resposta determinística de recomendação para testar a fronteira; não contém modelo treinado nem uma implementação completa de RAG.

## Alternativas e trade offs
A - IA embutida: menos rede, porém runtime e atualização de modelos prendem-se ao Core. B - Módulo acoplado/script local: interoperação possível, mas compartilha ciclo de implantação e recursos. C - Serviço Python: contrato remoto e falhas de rede, porém deploy e capacidade independentes. Separar ML e RAG já de início duplica operação sem medição; mantê-los juntos aceita disputa interna por recursos.

## Decisão
Escolhemos um serviço Python independente, com endpoints e responsabilidades separáveis. ML e RAG começam no mesmo limite operacional; extrair RAG se consumo de memória, latência ou cadência de atualização divergirem. O idioma Python sozinho não justifica um serviço: a decisão depende dos três ASRs.

## Consequências
Recomendação indisponível devolve catálogo básico; assistente deve informar indisponibilidade, nunca inventar resposta. Cliente Java tem timeout de conexão de 300 ms e de requisição de 800 ms, sem retries no caminho interativo. Instrumentamos um contador para cada fallback. Circuit breaker e limite de concorrência ficam para a AF, após observar falhas sustentadas.

## Evidência
ia-online.log e ia-offline.log registram o mesmo endpoint Spring respondendo 200: primeiro origem python, depois origem catalogo com o processo FastAPI interrompido. RecomendacaoTest exercita HTTP real em loopback: sucesso, erro 503 e serviço interrompido, verificando fallback e contador na AC1.

## Impacto C4
Container adiciona serviço Python e HTTP/JSON Core → IA. Vetores/documentos do RAG são capacidade futura, não artefatos do mini-lab.


# ADR-004 Comunicação e eventos

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
RF02 conclusão/recompensas e RF05 notificações. RNFs: tolerância a falhas, disponibilidade, latência, consistência, desacoplamento e recuperabilidade. Conclusão e recompensa local exigem confirmação imediata; notificação e analytics aceitam processamento eventual.

## ASRs prioritários
D4-A1 - Disponibilidade (R): Retirar notificação do caminho síncrono da conclusão. Verificação: Conclusão permanece gravada com consumidor indisponível.
D4-A2 - Consistência (R): Gravar conclusão e intenção de notificar na mesma transação. Verificação: Outbox SQLite persiste junto com conclusão.
D4-A3 - Recuperabilidade (R): Reentregar pendências e deduplicar identificadores no consumidor. Verificação: Reabertura do banco recupera evento; uma única entrega lógica.

## RPCs
Consumidor externo indisponível não pode impedir a conclusão; broker adiciona custo de operação. A microsolução usa SQLite para demonstrar atomicidade local e durabilidade, sem simular uma implantação completa em PostgreSQL.

## Alternativas e trade offs
A - REST síncrono: resposta imediata e fluxo simples, mas a indisponibilidade externa propaga falha. B - Outbox transacional e worker com entrega HTTP: grava intenção junto ao estado, permite retry, adiciona agendamento e controle de duplicidade. C - Outbox + RabbitMQ: útil para filas, roteamento e vários workers. Kafka favorece retenção/replay e múltiplos fluxos de consumo, mas exige governança e operação desproporcionais à notificação atual.

## Decisão
Decidimos manter conclusão/recompensa no Core e notificação eventual via outbox. Não adicionar RabbitMQ/Kafka neste estágio. O worker deve confirmar sucesso depois do consumidor e repetir pendências. Event ID idempotente no consumidor garante um único efeito mesmo com entrega repetida.

## Consequências
Sem exactly once: queda entre envio e marcação pode repetir a entrega. A AF deve persistir deduplicação no consumidor, definir backoff, limite de tentativas, fila de falhas e retenção. A notificação pode atrasar e o estado operacional de pendências precisa ser medido. O lab não altera ainda o fluxo de matrícula do Core.

## Evidência
microsolucoes/eventos.py: falha síncrona é propagada; conclusão+outbox permanecem gravadas quando a entrega falha; reabrir banco e reentregar recupera a pendência; repetir o mesmo event ID produz um único registro recebido.

## Impacto C4
Container alvo inclui worker de outbox e consumidor de notificação. A sequência distingue commit local de entrega posterior. Estes componentes estão no lab de eventos, ainda separados da aplicação Spring.


# ADR-005 IoT e MQTT

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
RF06 receber eventos IoT e encaminhá-los à plataforma. RNFs: interoperabilidade, eficiência, conectividade intermitente, escalabilidade e segurança. Os três ASRs escolhidos orientam protocolo, retry/deduplicação e fronteira de confiança.

## ASRs prioritários
D5-A1 - Eficiência de comunicação (P): Usar publicação MQTT com mensagens pequenas em vez de polling HTTP. Verificação: Publicador e consumidor QoS 1 executados em broker local.
D5-A2 - Conectividade intermitente (R): Exigir identificador estável, buffer e reenvio no dispositivo. Verificação: Duplicata testada; buffer persistente e sessão durável ainda são trabalho da AF.
D5-A3 - Segurança de integração (F+): Isolar tópico por instituição e validar identidade e payload no gateway. Verificação: Tópico institucional demonstrado; TLS/ACL ainda não exercitados no lab local.

## RPCs
Dispositivos podem ter CPU/memória limitadas e conexão instável. Broker e protocolo adicionam operação. O mini-lab usa broker real em loopback e modo anônimo apenas em ambiente local isolado.

## Alternativas e trade offs
A - REST direto: infraestrutura conhecida, mas dispositivos precisam implementar retry e conexão por requisição; Core fica exposto ao ritmo dos dispositivos. B - MQTT + gateway: publicação leve, QoS e desacoplamento; demanda broker, autenticação e gestão de sessão. C - MQTT + outro broker interno: justificável quando vários consumidores precisam replay/roteamento independente; hoje duplica a operação sem requisito demonstrado.

## Decisão
Escolhemos MQTT QoS 1 com gateway que transforma o contrato do dispositivo. Tópico por instituição, event_id estável e consumidor idempotente. Não adicionar outro broker. Em produção, usar TLS, credenciais por dispositivo, ACL por tópico, validação de esquema e deduplicação persistente antes de aplicar o evento no Core.

## Consequências
QoS 1 admite duplicatas e não substitui buffer persistente do dispositivo. O mini-lab demonstra deduplicação em memória e transformação, mas não comprova recuperação depois de reinício nem segurança multi-instituição. Esses itens são critérios de aceitação da AF, e não capacidades alegadas como já implementadas.

## Evidência
microsolucoes/mqtt_lab.py e mqtt.log: publicador envia duas cópias com QoS 1 ao broker; gateway recebe duas mensagens e gera uma PresencaRegistrada. A transformação ocorre antes da futura integração com o Core.

## Impacto C4
Container alvo: dispositivo → broker MQTT → gateway → Core. MQTT/TCP no lab; MQTT/TLS e HTTPS na implantação proposta.


# ADR-006 CI CD e observabilidade

Status: aceito para a PoC didática; implantação de produção não aprovada.
Data: 26/10/2026

## RF e RNF
Todos os RFs dependem de construção e operação confiáveis. RNFs: observabilidade, deploy independente, confiabilidade, testabilidade, recuperabilidade e desempenho. Foram selecionados observabilidade, confiabilidade de entrega e recuperabilidade operacional.

## ASRs prioritários
D6-A1 - Observabilidade (S): Instrumentar falhas recuperadas, além do status HTTP. Verificação: ia_fallback_total aumenta mesmo quando a API responde 200.
D6-A2 - Confiabilidade de entrega (R): Bloquear implantação quando testes ou build falharem. Verificação: Jenkinsfile sequencial sem ignorar falhas; Jenkins executou os testes, builds e deploy do laboratório; 64 testes aprovados, nenhum ignorado.
D6-A3 - Recuperabilidade operacional (R): Versionar imagens/configuração e preparar retorno à versão anterior. Verificação: JAR e configurações versionáveis; rollback em ambiente Jenkins ainda pendente.

## RPCs
Jenkins é a base da esteira e Prometheus/Grafana são ferramentas do laboratório. Artefatos e configuração versionados. Esta entrega inclui a evolução pedida no B1; a atividade A2 de Jenkins não foi refeita nem sobrescrita.

## Alternativas e trade offs
A - Pipeline único: integração simples e coordenação central, mas mudanças na IA podem aguardar etapas do Core. B - Pipelines por componente: deploy independente e menor escopo de falha, com custo de contratos e promoção conjunta. Com um único repositório e equipe pequena, o pipeline inicial único tem etapas separadas; extrair pipelines quando a frequência independente for comprovada.

## Decisão
Executamos um Jenkinsfile com teste/build antes de Docker e smoke test após implantação. Falhas Maven bloqueiam deploy; falha de saúde falha a execução. Prometheus coleta Actuator e Grafana consulta Prometheus. O contador ia_fallback_total evidencia degradação invisível ao HTTP 200.

## Consequências
Jenkinsfile usa agente Linux com JDK 17 para o projeto, Python e Docker. O workflow inicia Jenkins com Java 21 e executa o Jenkinsfile obtido do Git. A validação inclui PostgreSQL real, build, cobertura, containers, APIs e métricas. O resultado efetivo e a proveniência devem ser consultados no artefato da execução. Não há rollback automático implementado; a AF deve usar tags imutáveis e validar retorno à versão anterior.

## Evidência
Execução https://github.com/Gustavo-Champam/Exercicio-B1-Arquitetura-de-Software-AC2/actions/runs/41537017845, resultado Jenkins SUCCESS. evidencias/jenkins/provenance.json vincula o resultado ao commit; junit.json registra 64 testes aprovados e nenhum ignorado. Os relatórios JaCoCo dos dois projetos registram 100% de linhas e ramos. validacao.json registra ranking real de 1203 pontos, IA online e fallback após parar o container, além dos contadores Prometheus. As capturas b1-grafana.png e a3-grafana.png mostram os painéis atuais. up comprova coleta, não disponibilidade funcional completa. Histogramas permitem medir latência; não alegamos teste de 100 mil usuários.

## Impacto C4
Deployment representa Jenkins no runner temporário, containers da AC1, FastAPI, Prometheus/Grafana, PostgreSQL de teste e aplicação A3. Eventos e MQTT continuam microsoluções que comprovam as hipóteses dos desafios 4 e 5.
