# Desafio Votação

Aplicação full-stack para cadastrar pautas, abrir sessões, receber votos e
consultar resultados. O projeto foi desenvolvido para o
[desafio de votação full-stack](https://github.com/somosdb/desafio-votacao-fullstack).

## Funcionalidades

- cadastro e consulta de pautas;
- abertura de uma sessão por pauta;
- duração informada na abertura ou um minuto por padrão;
- votos `SIM` e `NAO`;
- um único voto por CPF em cada pauta;
- validação de CPF e autorização por cliente falso;
- persistência dos dados no PostgreSQL.

## Tecnologias

- backend: Java 21, Spring Boot 4, Spring Data JPA, Flyway e PostgreSQL 17;
- frontend: Angular 21, Angular Material e Nginx;
- testes: JUnit, MockMvc, Testcontainers, Vitest e Grafana k6;
- execução: Docker e Docker Compose.

## Executar com Docker

É necessário ter Docker Engine e Docker Compose instalados.

Na raiz do repositório:

```bash
docker compose up --build -d
docker compose ps
```

Aguarde o banco ficar 'healthy' e acesse:

- aplicação: <http://localhost:4200>
- Swagger: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI: <http://localhost:8080/v3/api-docs>

Para consultar os logs:

```bash
docker compose logs -f frontend backend
```

Para encerrar sem apagar os dados:

```bash
docker compose down
```

Os dados ficam armazenados em uma base do PostgreSQL. `docker compose down -v` remove essa base e deve ser usado apenas
caso queira reiniciar o banco do zero.

## Desenvolvimento local

### Backend

Com Java 21 e Docker ativos, suba somente o banco:

```bash
docker compose up -d banco-de-dados

cd backend
./mvnw spring-boot:run
```

A API ficará disponível em <http://localhost:8080>. As configurações padrão do
banco podem ser substituídas pelas variáveis `URL_BANCO`, `USUARIO_BANCO` e
`SENHA_BANCO`.

### Frontend

Com Node.js 24 e npm instalados, deixe o backend em execução e abra outro
terminal:

```bash
cd frontend
npm ci
npm start
```

A aplicação ficará disponível em <http://localhost:4200>. Durante o
desenvolvimento, as requisições para `/api` são encaminhadas ao backend pelo
proxy do Angular.

## API

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/v1/pautas` | cadastrar pauta |
| `GET` | `/api/v1/pautas` | listar pautas |
| `GET` | `/api/v1/pautas/{id}` | consultar pauta |
| `POST` | `/api/v1/pautas/{id}/sessao` | abrir sessão |
| `GET` | `/api/v1/pautas/{id}/sessao` | consultar sessão |
| `POST` | `/api/v1/pautas/{id}/votos` | registrar voto |
| `GET` | `/api/v1/pautas/{id}/resultado` | consultar resultado |

Os contratos e exemplos de requisição podem ser consultados no Swagger.

## Regras principais

- cada pauta pode ter somente uma sessão;
- a duração deve ser informada em minutos;
- quando a duração não é enviada, a sessão permanece aberta por um minuto;
- o CPF deve ter 11 dígitos válidos;
- cada CPF pode votar uma única vez por pauta;
- votos são aceitos somente durante uma sessão aberta;
- o resultado pode ser `APROVADA`, `REPROVADA`, `EMPATE` ou `SEM_VOTOS`;
- o cliente falso retorna aleatoriamente `ABLE_TO_VOTE` ou
  `UNABLE_TO_VOTE`.

CPF inválido ou associado não autorizado retornam `404`, conforme a tarefa
bônus. Erros de validação retornam `400` e conflitos de regra de negócio
retornam `409`.

## Testes

Os testes do backend utilizam Testcontainers, portanto o Docker precisa estar
ativo:

```bash
cd backend
./mvnw test
```

Testes e build do frontend:

```bash
cd frontend
npm ci
npm test -- --watch=false
npm run build
```

## Teste de desempenho

O cenário k6 cria uma pauta e uma sessão e registra votos concorrentes. Primeiro
deixe banco e backend em execução:

```bash
docker compose up -d banco-de-dados backend
```

Teste rápido:

```bash
QUANTIDADE_VOTOS=1000 USUARIOS_VIRTUAIS=20 \
docker compose --profile desempenho run --rm teste-desempenho
```

Cenário com cem mil votos:

```bash
QUANTIDADE_VOTOS=100000 USUARIOS_VIRTUAIS=100 \
docker compose --profile desempenho run --rm teste-desempenho
```

As recusas de autorização são esperadas porque o cliente falso responde
aleatoriamente. O script repete a operação até registrar o voto ou atingir o
limite configurado.

## Arquitetura e decisões

O backend utiliza arquitetura em camadas, com controladores, serviços,
repositórios, entidades e DTOs. O Flyway controla o banco e constraints
garantem a integridade de votos concorrentes.

O sistema não utiliza autenticação ou Spring Security porque o desafio permite
abstrair a segurança. A API é versionada pela URI, usando o prefixo
`/api/v1`. Backend e frontend ficam no mesmo repositório para simplificar entrega, dessa mesma forma, também não foram usadas branchs para cada feature

_______________________________________________________________________________________________________________________________________________________________________________

# Votação

## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução we para gerenciar e participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST / Front:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
  um tempo determinado na chamada de abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
  é identificado por um id único e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser considerada como autorizada. A solução deve ser construída em java com Spring-boot e Angular/React conforme orientação, mas os frameworks e bibliotecas são de livre escolha (desde que não infrinja direitos de uso).

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

## Como proceder

Por favor, realize o FORK desse repositório e implemente sua solução no FORK em seu repositório GItHub, ao final, notifique da conclusão para que possamos analisar o código implementado.

Lembre de deixar todas as orientações necessárias para executar o seu código.

### Tarefas bônus

- Tarefa Bônus 1 - Integração com sistemas externos
  - Criar uma Facade/Client Fake que retorna aleátoriamente se um CPF recebido é válido ou não.
  - Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos
  - Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação. Essa operação retorna resultados aleatórios, portanto um mesmo CPF pode funcionar em um teste e não funcionar no outro.

```
// CPF Ok para votar
{
    "status": "ABLE_TO_VOTE
}
// CPF Nao Ok para votar - retornar 404 no client tb
{
    "status": "UNABLE_TO_VOTE
}
```

Exemplos de retorno do serviço

### Tarefa Bônus 2 - Performance

- Imagine que sua aplicação possa ser usada em cenários que existam centenas de
  milhares de votos. Ela deve se comportar de maneira performática nesses
  cenários
- Testes de performance são uma boa maneira de garantir e observar como sua
  aplicação se comporta

### Tarefa Bônus 3 - Versionamento da API

○ Como você versionaria a API da sua aplicação? Que estratégia usar?

## O que será analisado

- Simplicidade no design da solução (evitar over engineering)
- Organização do código
- Arquitetura do projeto
- Boas práticas de programação (manutenibilidade, legibilidade etc)
- Possíveis bugs
- Tratamento de erros e exceções
- Explicação breve do porquê das escolhas tomadas durante o desenvolvimento da solução
- Uso de testes automatizados e ferramentas de qualidade
- Limpeza do código
- Documentação do código e da API
- Logs da aplicação
- Mensagens e organização dos commits
- Testes
- Layout responsivo

## Dicas

- Teste bem sua solução, evite bugs

  Observações importantes
- Não inicie o teste sem sanar todas as dúvidas
- Iremos executar a aplicação para testá-la, cuide com qualquer dependência externa e
  deixe claro caso haja instruções especiais para execução do mesmo
  Classificação da informação: Uso Interno

