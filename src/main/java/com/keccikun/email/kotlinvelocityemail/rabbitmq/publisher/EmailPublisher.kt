package com.keccikun.email.kotlinvelocityemail.rabbitmq.publisher

import com.keccikun.email.kotlinvelocityemail.config.RabbitConfig
import com.keccikun.email.kotlinvelocityemail.service.EmailService
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeoutException

open class EmailPublisher constructor(var rabbitConfig: RabbitConfig) {
    internal var logger = LoggerFactory.getLogger(EmailPublisher::class.java)

    @Throws(IOException::class, TimeoutException::class)
    fun publish(payload: String?) {
        val connection = rabbitConfig.getConnection()
        val channel = connection?.createChannel()
        channel?.basicPublish(EmailService.EXCHANGE_NAME, EmailService.EMAIL_ROUTE_KEY, null, payload?.toByteArray(Charset.forName("UTF-8")))
        logger.info(" [x] publish email : {}", payload)
        channel?.close()
        connection?.close()
    }
}