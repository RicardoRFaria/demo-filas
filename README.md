# Demo Filas

Inicie o sistema de filas com `docker-compose up`

Para interagir via console você vai precisar de do `aws-cli` instalado e um perfil de teste, para criá-lo os comandos são os seguintes

```
echo [demo-filas] >> ~/.aws/credentials
echo aws_access_key_id=test-key >> ~/.aws/credentials
echo aws_secret_access_key=test-secret >> ~/.aws/credentials
export AWS_PROFILE=demo-filas
```

## Sistema administrativo

Depois de iniciado a imagem docker, você pode abrir o sistema administrativo das filas com através do link http://localhost:15672/

Os usuários default são `guest` e senha `guest`

## Request para o endpoint de envio de mensagem simples

http://localhost:8080/send-simple-message?texto=meutextodebomdia