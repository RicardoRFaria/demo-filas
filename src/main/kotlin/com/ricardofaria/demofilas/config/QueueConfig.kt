package com.ricardofaria.demofilas.config

import com.ricardofaria.demofilas.SimpleReceiver
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class QueueConfig {

    companion object {
        const val TOPIC_EXCHANGE_NAME = "simple-exchange"
        const val QUEUE_NAME = "simple-queue"
    }

    @Bean("simpleQueue")
    fun queue(): Queue {
        return Queue(QUEUE_NAME, false)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(TOPIC_EXCHANGE_NAME)
    }

    @Bean
    fun binding(queue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#")
    }

    @Bean
    fun container(connectionFactory: ConnectionFactory,
                  listenerAdapter: MessageListenerAdapter): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setQueueNames(QUEUE_NAME)
        container.setMessageListener(listenerAdapter)
        return container
    }

    @Bean
    fun listenerAdapter(simpleReceiver: SimpleReceiver): MessageListenerAdapter {
        return MessageListenerAdapter(simpleReceiver, "receiveMessage")
    }


}