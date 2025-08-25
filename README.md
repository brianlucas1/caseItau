# Case Itaú — API de Dados Pessoais
API em Java 17 + Spring Boot que cadastra dados pessoais, persiste em PostgreSQL e agenda uma notificação por e-mail após 2 minutos via RabbitMQ.
O acesso externo passa por Spring Cloud Gateway (circuit breaker, retry e correlation-id).
Observability com Prometheus + Grafana (métricas) e Loki + Promtail (logs centralizados).

#Arquitetura

Gateway (8081) → roteia /api/** para a API interna (app:8080), adiciona X-Correlation-Id, aplica Circuit Breaker (Resilience4j) e Retry (GET).

API (8080, apenas rede interna) → CRUD de /api/dados-pessoais, persiste no PostgreSQL, agenda +2 min e publica na fila.
 Um consumer lê e dispara e-mail.

Observability → Prometheus “scrapa” métricas do Gateway e da API;
 Promtail coleta logs dos containers e envia ao Loki; Grafana unifica dashboards e busca de logs.


# Utilização - Deve ter o docker instalado na máquina.

Clone o projeto

Dentro da pasta de onde fez o clone, existe um arquivo chamado docker compose.

Abra o terminal  C:download/projeto

Rode o seguinte comando para iniciar a imagem : docker compose up -d --build

Portas para testes 

Portas externas (host):

Gateway: http://localhost:8081

RabbitMQ UI: http://localhost:15672

Prometheus: http://localhost:9090

Grafana: http://localhost:3000

# Testes da api 

CADASTRO -  POST  http://localhost:8081/api/dados-pessoais

{
  "nome": "brian",
  "sobreNome": "lucas",
  "idade": 30,
  "pais": "Brasil",
  "email": "n1@n1.com"
}

BUSCA POR ID - GET http://localhost:8081/api/dados-pessoais/1

PATH - http://localhost:8080/api/dados-pessoais/1
 pode ser enviado o exemplo de json a cima para atualizar uma pessoa apartir do id passado na url 
 Ou somente enviado um atributo para mudança.
{
  "nome": "teste2 " 
}

Busca todos - GET http://localhost:8081/api/dados-pessoais





