# Teste de desempenho

O teste k6 é realizado pelo Docker Compose.

Cada iteração gera um CPF válido e exclusivo e tenta registrar um voto. Como o
cliente facede de autorização da uma resposta aleatoria, o teste repete a chamada
quando recebe `404` até que o voto seja aceito ou o limite de tentativas seja
atingido.

## Executar verificação 

Na raiz do sistema:

```bash
QUANTIDADE_VOTOS=100000 USUARIOS_VIRTUAIS=100 \
docker compose --profile desempenho run --rm teste-desempenho
```

Variáveis:

- `QUANTIDADE_VOTOS`: quantidade de votos que o teste tentará registrar;
- `USUARIOS_VIRTUAIS`: quantidade de usuários concorrentes;
- `MAXIMO_TENTATIVAS_AUTORIZACAO`: tentativas diante da recusa aleatória;
- `DURACAO_MAXIMA`: tempo máximo do cenário, com padrão de `30m`.

O teste será considerado aprovado quando:

- não houver respostas HTTP inesperadas;
- pelo menos 99% dos votos forem registrados;
- 95% das chamadas de votação responderem em menos de um segundo.

Os resultados dependem dos recursos da máquina. Os limites definidos são uma
referência inicial para detectar regressões, e não um SLA de produção.
