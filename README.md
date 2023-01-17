# Demo Filas

1. Inicie o docker com `docker-compose up`
2. Execute o comando para iniciar a aplicação `mvnw spring-boot:run`

# Collection de postman
Você pode importar a collection do postman que existe aqui no projeto através do arquivo `demo-filas.postman_collection.json`

## Requests para as filas
Para o endpoint de envio de mensagem simples

GET http://localhost:8080/send-simple-message?texto=meutextodebomdia

Para o endpoint em que a mensagem nunca é consumida a tempo e expira por visibility timeout

GET http://localhost:8080/send-message-for-queue-with-waiting?texto=meu-texto-que-nunca-finaliza&numeroDeParadas=10

Para o endpoint que falha no consumo, mas tem DLQ

GET http://localhost:8080/send-message-for-queue-with-dlq?texto=minha-mensagem-que-e-consumida&numeroDeParadas=5

Para o endpoint que tem backpressure baseado em estado da tabase

GET http://localhost:8080/send-message-for-queue-with-health-check-backpressure?texto=Mensagemcombackpressure

Para o endpoint que tem rate limit

GET http://localhost:8080/send-message-for-queue-with-rate-limit?texto=Mensagemcomratelimit1

## Requests para o fluxo de item

Para obter todos os itens

GET http://localhost:8080/items



