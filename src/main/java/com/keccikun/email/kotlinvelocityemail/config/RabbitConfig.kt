package com.keccikun.email.kotlinvelocityemail.config

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException

@Configuration
open class RabbitConfig @Autowired
constructor(private val properties: AppProperties) {

    var channel: Channel? = null
        private set
    var factory : ConnectionFactory? = null
    val es = Executors.newFixedThreadPool(20)
    init {
        factory = ConnectionFactory()
        factory?.host = properties.rabbit?.url
        factory?.username = properties.rabbit?.username
        factory?.password = properties.rabbit?.password
        try {
            this.channel = factory?.newConnection(es)?.createChannel()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

    }

    fun getConnection(): Connection? {
        return factory?.newConnection(es)
    }


}
