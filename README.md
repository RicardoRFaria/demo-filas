# Demo Filas

Inicie o sistema de filas com `docker-compose up`

## Requests
Para o endpoint de envio de mensagem simples

http://localhost:8080/send-simple-message?texto=meutextodebomdia

Para o endpoint em que a mensagem nunca é consumida a tempo e expira por visibility timeout

http://localhost:8080/send-message-for-queue-with-waiting?texto=meu-texto-que-nunca-finaliza&numeroDeParadas=10

Para o endpoint que falha no consumo, mas tem DLQ

http://localhost:8080/send-message-for-queue-with-dlq?texto=minha-mensagem-que-e-consumida&numeroDeParadas=5

Para o endpoint que tem backpressure baseado em estado da tabase

http://localhost:8080/send-message-for-queue-with-health-check-backpressure?texto=Mensagemcombackpressure

Para o endpoint que tem rate limit

http://localhost:8080/send-message-for-queue-with-rate-limit?texto=Mensagemcomratelimit1