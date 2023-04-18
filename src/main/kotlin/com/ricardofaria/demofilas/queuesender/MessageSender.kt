package com.ricardofaria.demofilas.queuesender

import com.google.gson.Gson
import com.ricardofaria.demofilas.messagecontracts.MessageWithDelay
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest


@Service
class MessageSender(
    private val sqsClient: SqsClient,
    @Value("\${aws.sqs.simplesqswithconsuming.url}") private val simpleSqsWithConsuming: String,
    @Value("\${aws.sqs.queuewithdelay.url}") private val queueWithDelay: String,
    @Value("\${aws.sqs.queuewithdlq.url}") private val queueWithDLQ: String,
    @Value("\${aws.sqs.queuewithbackpressure}") private val queueWithBackpressure: String,
    @Value("\${aws.sqs.queuewithratelimit}") private val queueWithRateLimit: String,
) {

    fun sendSimpleMessage(texto: String) {
        println("Enviando mensagem com o texto recebido: $texto")
        val messageRequest = SendMessageRequest.builder()
            .queueUrl(simpleSqsWithConsuming)
            .messageBody(texto)
            .build()
        sqsClient.sendMessage(messageRequest)
        println("Mensagem enviada")
    }

    fun sendMessageToDelayedQueue(texto: String, numeroDeParadas: Int) {
        println("Enviando mensagem com o texto recebido: $texto")
        val messageContract = MessageWithDelay(texto, numeroDeParadas)
        val messageContractAsJSON = Gson().toJson(messageContract)
        val messageRequest = SendMessageRequest.builder()
            .queueUrl(queueWithDelay)
            .messageBody(messageContractAsJSON)
            .build()
        sqsClient.sendMessage(messageRequest)
        println("Mensagem com delay enviada")
    }

    fun sendMessageToQueueWithDLQ(texto: String, numeroDeParadas: Int) {
        println("Enviando mensagem com o texto recebido: $texto")
        val messageContract = MessageWithDelay(texto, numeroDeParadas)
        val messageContractAsJSON = Gson().toJson(messageContract)
        val messageRequest = SendMessageRequest.builder()
            .queueUrl(queueWithDLQ)
            .messageBody(messageContractAsJSON)
            .build()
        sqsClient.sendMessage(messageRequest)
        println("Mensagem da fila com DLQ enviada")
    }

    fun sendMessageForQueueWithHealthCheckBackpressure(texto: String) {
        sendTextMessage(texto, queueWithBackpressure)
        println("Mensagem enviada: <$texto> para a fila com health check backpressure")
    }

    fun sendMessageForQueueWithRateLimit(texto: String) {
        sendTextMessage(texto, queueWithRateLimit)
        println("Mensagem enviada: <$texto> para a fila com rate limit")
    }

    fun sendTextMessage(text: String, queue: String) {
        val messageRequest = SendMessageRequest.builder()
            .queueUrl(queue)
            .messageBody(text)
            .build()
        sqsClient.sendMessage(messageRequest)
    }

}