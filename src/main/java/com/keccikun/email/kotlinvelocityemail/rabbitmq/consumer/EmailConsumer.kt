package com.keccikun.email.kotlinvelocityemail.rabbitmq.consumer

import com.keccikun.email.kotlinvelocityemail.config.RabbitConfig
import com.keccikun.email.kotlinvelocityemail.rabbitmq.executor.EmailExecutor
import com.keccikun.email.kotlinvelocityemail.service.EmailService
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.util.concurrent.TimeoutException

@Configuration
open class EmailConsumer @Autowired
@Throws(IOException::class, TimeoutException::class)
constructor(emailNotificationExecutor: EmailExecutor?, rabbitConfig: RabbitConfig) {
    internal var logger = LoggerFactory.getLogger(EmailConsumer::class.java)

    init {

        val channel = rabbitConfig.channel
        channel?.exchangeDeclare(EmailService.EXCHANGE_NAME, "topic", true)
        channel?.queueDeclare(EmailService.EMAIL_QUEUE, true, false, false, null)
        channel?.queueBind(EmailService.EMAIL_QUEUE, EmailService.EXCHANGE_NAME, EmailService.EMAIL_ROUTE_KEY)
        channel?.basicQos(20)

        logger.info(" [*] Email MQ ready for messages!")

        val consumer = object : DefaultConsumer(channel) {

            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray) {
                try {
                    emailNotificationExecutor?.execute(String(body))
                } catch (e: Exception) {
                    logger.error(e.message, e)
                } finally {
                    channel?.basicAck(envelope!!.deliveryTag, false)
                }
            }
        }

        val autoAck = false
        channel?.basicConsume(EmailService.EMAIL_QUEUE, autoAck, consumer)
    }
}