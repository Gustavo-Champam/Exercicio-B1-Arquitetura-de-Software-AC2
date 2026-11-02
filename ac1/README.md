# Educação Continuada Gamificada

Projeto desenvolvido para aplicar na prática os conceitos de **ATDD, BDD e TDD**, utilizando **Spring Boot, JPA, H2, PostgreSQL, Swagger, Vue e Docker**.

## Sobre o projeto

O projeto simula uma plataforma de educação continuada em que os alunos podem concluir cursos e evoluir dentro da plataforma.

A regra principal definida no estudo de caso é:

* O aluno começa com o plano **Básico**;
* Para passar para o plano **Premium**, precisa concluir **12 cursos diferentes**;
* Cada curso precisa ter média **igual ou superior a 7,0**;
* Cursos com nota menor que 7,0 não contam para o progresso;
* Caso o aluno não seja aprovado, ele pode tentar o curso novamente;
* Um mesmo curso aprovado só pode ser contabilizado uma vez;
* Ao chegar aos 12 cursos válidos, o aluno é promovido automaticamente para Premium;
* Depois da promoção, recebe **1 voucher de projeto real e 3 moedas virtuais**.

As recompensas também ficam registradas com sua origem e data de concessão.

## Integrantes e histórias de usuário

| Integrante          | História de usuário                                                                                                                                                                |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Gustavo Camargo** | **US01 — Aluno Básico:** Como aluno com assinatura básica, quero ser promovido automaticamente ao Premium ao concluir 12 cursos válidos, para acessar os benefícios do novo plano. |
| **Gustavo Champam** | **US02 — Aluno Premium:** Como aluno recém-promovido, quero receber um voucher e três moedas em minha carteira, para ter acesso às recompensas da plataforma.                      |

A história escolhida inicialmente foi a **US01**, mas na versão final do projeto as duas histórias foram integradas.

A branch `feat/us01-promocao-aluno` foi integrada à `main`, mantendo o histórico de desenvolvimento e os testes criados durante as etapas de RED, GREEN e refatoração.

Nesta entrega foram implementadas a concessão e a consulta das recompensas do aluno Premium.

Funcionalidades como resgate de voucher, conversão de moedas ou recompensas relacionadas a fórum não fazem parte dos critérios de aceite deste trabalho.

## BDD e ATDD

Os cenários e materiais utilizados durante o desenvolvimento estão disponíveis na pasta `documentacao`.

* [BDD da US01 — Gustavo Camargo](documentacao/BDD-US01.md)
* [BDD da US02 — Gustavo Champam](documentacao/BDD-US02.md)
* [Planilha ATDD](documentacao/Template_ATDD.xlsx)

Os critérios de aceite foram transformados em testes automatizados utilizando **JUnit 5 e MockMvc**.

Os testes passam pelas principais camadas da aplicação:

`Controller → Service → Domain → JPA → Banco`

Durante os testes padrão é utilizado o banco **H2**.

Também existe o teste `AlunoPostgresTest`, utilizado para verificar o funcionamento com um banco **PostgreSQL real**.

Os testes da US02 foram criados antes da implementação das recompensas, permitindo registrar a etapa RED do TDD.

## Como executar o projeto no IntelliJ

### Back-end

1. Abrir o projeto no IntelliJ.
2. Importar o `pom.xml` como projeto Maven.
3. Configurar o projeto para utilizar **Java 17**.
4. Recarregar as dependências do Maven.
5. Executar a classe:

`EducacaoGamificadaApplication`

Por padrão, a aplicação utiliza o banco H2.

Depois que a aplicação iniciar, o Swagger pode ser acessado em:

`http://localhost:8080/swagger-ui/index.html`

## Front-end

Em outro terminal:

```sh
cd frontend
npm ci
npm run dev
```

Depois disso, acessar:

`http://localhost:5173`

O projeto utiliza:

* Java 17
* Node.js 22.12 ou superior
* Vue
* Vite

O proxy do Vite encaminha as chamadas `/api` para o Spring Boot na porta `8080`.

## Executando os testes

Para executar todos os testes e gerar a cobertura:

```sh
mvn clean verify
```

Também é possível executar os testes diretamente pelo IntelliJ.

Para visualizar a cobertura pela IDE, pode ser utilizada a opção **Run with Coverage**.

O relatório de cobertura do JaCoCo também é gerado em:

`target/site/jacoco/index.html`

## Docker e PostgreSQL

O ambiente completo também pode ser executado com Docker.

```sh
docker compose up --build -d
```

Depois da inicialização:

| Serviço    | Endereço                                      |
| ---------- | --------------------------------------------- |
| Vue        | `http://localhost:5173`                       |
| Swagger    | `http://localhost:8080/swagger-ui/index.html` |
| pgAdmin    | `http://localhost:5050`                       |
| PostgreSQL | `localhost:5432`                              |

Banco e usuário do PostgreSQL:

`educacao`

Credenciais utilizadas no ambiente local:

**PostgreSQL**

`educacao_local`

**pgAdmin**

Usuário:

`admin@educacao.com`

Senha:

`admin_local`

Caso seja necessário alterar essas configurações, basta copiar o arquivo `.env.example` para `.env` antes da primeira execução.

Dentro do Docker, o nome utilizado para acessar o banco PostgreSQL é:

`postgres`

## Demonstração utilizando H2

Também foi criado um perfil de demonstração que executa uma segunda instância da aplicação utilizando H2.

```sh
docker compose --profile demonstracao up --build -d
```

Swagger da versão H2:

`http://localhost:8081/swagger-ui/index.html`

Console H2:

`http://localhost:8081/h2-console`

Configurações:

```text
JDBC: jdbc:h2:mem:educacao
Usuário: sa
Senha: vazia
```

Para encerrar os containers:

```sh
docker compose --profile demonstracao down
```

O H2 utiliza dados temporários, enquanto o PostgreSQL utiliza volume para manter os dados.

## Endpoints

| Método | Endpoint                       | Função                           |
| ------ | ------------------------------ | -------------------------------- |
| POST   | `/api/alunos`                  | Cadastra um aluno Básico         |
| GET    | `/api/alunos`                  | Lista os alunos                  |
| GET    | `/api/alunos/{id}`             | Consulta um aluno                |
| POST   | `/api/alunos/{id}/conclusoes`  | Registra a conclusão de um curso |
| GET    | `/api/alunos/{id}/recompensas` | Consulta as recompensas do aluno |

### Cadastrar aluno

Exemplo:

```json
{
  "nome": "Gustavo"
}
```

Um novo aluno é criado como **Básico**, sem cursos concluídos e sem recompensas.

### Registrar conclusão de curso

Exemplo:

```json
{
  "codigoCurso": "JAVA-01",
  "media": 7.0
}
```

Caso a média seja igual ou maior que 7, o curso é contabilizado.

Caso seja menor que 7:

```text
cursoContabilizado: false
```

Quando o aluno conclui o 12º curso válido, a resposta informa:

```text
promovidoAgora: true
plano: PREMIUM
vouchers: 1
moedas: 3
```

Caso o mesmo curso seja enviado novamente após já ter sido aprovado, a aplicação retorna **HTTP 409**.

Dados inválidos retornam **HTTP 400**.

Os códigos dos cursos também são padronizados, removendo espaços no início e no final e convertendo o texto para letras maiúsculas.

## Script de verificação HTTP

Também existe um script para facilitar a demonstração dos principais fluxos da aplicação.

```sh
python scripts/verificar-http.py http://localhost:8080 evidencias/execucao/http.json
```

O script cria um novo aluno e executa os testes de funcionamento da API.

## Consultas no banco

Para verificar os dados pelo H2 ou pgAdmin podem ser utilizadas as consultas abaixo:

```sql
SELECT id, nome, plano, cursos_concluidos, vouchers, moedas
FROM alunos
ORDER BY id;
```

```sql
SELECT aluno_id, codigo_curso, media
FROM conclusoes_cursos
ORDER BY aluno_id, codigo_curso;
```

```sql
SELECT aluno_id, vouchers, moedas, origem, registrado_em
FROM recompensas_premium
ORDER BY aluno_id;
```

## Organização do projeto

O projeto foi separado em camadas para facilitar a manutenção e os testes.

### Domain

Contém as regras principais do sistema:

* `Aluno`
* `Plano`

Essa parte não depende diretamente do Spring ou do JPA.

### Entity

Responsável pelas entidades utilizadas na persistência.

### Repository

Responsável pelo acesso ao banco de dados.

### Service

Contém os serviços e regras utilizadas nos casos de uso.

### DTO

Contém os objetos utilizados na entrada e saída da API.

### Controller

Responsável pelos endpoints HTTP.

Os testes individuais também foram separados de acordo com as histórias.

US01:

`AlunoTest.java`

US02:

`AlunoPremiumTest.java`

## Controle de duplicidade

Também foram adicionadas proteções para evitar que o mesmo curso seja contabilizado mais de uma vez.

Durante o registro da conclusão, o aluno é bloqueado para atualização enquanto a transação está acontecendo.

Quando ocorre a promoção, é publicado um:

`AlunoPromovidoEvent`

O `RecompensaService` recebe esse evento e registra as recompensas do aluno.

Além das verificações na aplicação, o banco também possui restrições para evitar registros duplicados.

Dessa forma:

* um mesmo curso aprovado não é contabilizado duas vezes;
* as recompensas da promoção não são adicionadas mais de uma vez;
* a promoção acontece somente quando os critérios definidos são atendidos.

## Ciclo TDD: RED, GREEN e BLUE

Durante o desenvolvimento foram registradas evidências das diferentes etapas do TDD.

| Etapa               | Evidência                                       | Resultado                                |
| ------------------- | ----------------------------------------------- | ---------------------------------------- |
| RED US01            | `2a76de7` / `evidencias/red/us01-junit.log`     | Testes falhando conforme esperado        |
| GREEN US01          | `834faae` / `evidencias/green/us01-junit.log`   | 12 testes aprovados                      |
| BLUE US01           | `f83e7ba`                                       | Refatoração mantendo os testes aprovados |
| RED US02            | `d9a87e9` / [log](evidencias/red/us02-junit.log) / [IntelliJ](evidencias/red/us02-red-intellij.png) | 3 testes executados e 3 falhas esperadas |
| RED integração US02 | `cccb7dd` / `evidencias/red/us02-aceitacao.log` | 20 testes, sendo 5 falhas esperadas      |
| GREEN US02          | `bfb70cc` / [log](evidencias/green/us02-junit.log) / [IntelliJ](evidencias/green/us02-green.png) | Testes aprovados                         |
| BLUE final          | [verificação completa](evidencias/blue/verificacao-completa.log) | 37 testes aprovados                      |
| Cobertura           | [relatório JaCoCo](evidencias/blue/cobertura/index.html) / [captura](evidencias/blue/cobertura/us02-blue-cobertura-100.png) | 100% de instruções, branches, linhas, métodos e classes |
| Front-end           | `evidencias/blue/vue-e2e.log`                   | Fluxos da interface testados             |

Na verificação final foram executados:

**37 testes**

Resultado:

* 0 falhas
* 0 erros
* 0 testes ignorados

O relatório JaCoCo apresentou **100% de cobertura de instruções, linhas, branches e métodos nas 16 classes Java analisadas**.

A cobertura foi utilizada como uma evidência complementar. Os critérios de aceite continuam sendo validados principalmente pelos testes definidos a partir das histórias de usuário.

## Reproduzindo RED e GREEN

É possível acessar versões anteriores do projeto sem alterar a versão atual utilizando `git worktree`.

```sh
git worktree add ../educacao-red d9a87e9
git worktree add ../educacao-green bfb70cc
```

Executar RED:

```sh
mvn -f ../educacao-red/pom.xml -Dtest=AlunoPremiumTest test
```

Nesta versão os testes devem falhar.

Executar GREEN:

```sh
mvn -f ../educacao-green/pom.xml -Dtest=AlunoPremiumTest test
```

Nesta versão os testes devem passar.

Para verificar a versão final:

```sh
mvn clean verify
```

Depois, abrir:

`target/site/jacoco/index.html`

## Teste com PostgreSQL

Para executar também o teste utilizando PostgreSQL:

```powershell
$env:TESTAR_POSTGRES = "true"
$env:DB_URL = "jdbc:postgresql://localhost:5432/educacao"
$env:DB_USER = "educacao"
$env:DB_PASSWORD = "educacao_local"

mvn clean verify
```

Quando `TESTAR_POSTGRES` não está definido como `true`, o teste de PostgreSQL não é executado.

## GitHub Actions

O projeto também possui uma pipeline no GitHub Actions responsável por verificar:

* testes do back-end;
* execução com H2;
* execução com PostgreSQL;
* build do Vue;
* ambiente Docker;
* pgAdmin;
* testes da interface.

GitHub Actions:

https://github.com/Gustavo-Champam/educacao-continuada-gamificada/actions

Durante os testes locais tivemos um problema com o Docker Desktop da máquina, que não estava inicializando corretamente.

Por esse motivo, o ambiente Docker completo também foi validado pelo GitHub Actions.

Execução utilizada como evidência:

https://github.com/Gustavo-Champam/educacao-continuada-gamificada/actions/runs/34880100641

Os jobs de **backend, frontend e Docker** foram concluídos com sucesso.

As evidências utilizadas na entrega estão organizadas dentro da pasta:

`evidencias`

Também existe um índice com os arquivos disponíveis em:

`evidencias/README.md`

## Tecnologias utilizadas

* Java 17
* Spring Boot
* Spring Data JPA
* Maven
* JUnit 5
* MockMvc
* H2
* PostgreSQL
* Swagger / OpenAPI
* Vue
* Vite
* Docker
* Docker Compose
* pgAdmin
* JaCoCo
* GitHub Actions

## Resultado final

O projeto final permite cadastrar alunos, registrar a conclusão dos cursos, acompanhar o progresso e realizar automaticamente a promoção para o plano Premium.

Quando o aluno cumpre os critérios definidos no estudo de caso, a aplicação também adiciona as recompensas previstas e mantém um histórico dessas informações.

As histórias **US01 e US02** foram integradas na versão final e seus principais critérios de aceite foram cobertos pelos testes automatizados.
