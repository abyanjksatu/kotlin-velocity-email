package com.keccikun.email.kotlinvelocityemail.rabbitmq.executor

import com.keccikun.email.kotlinvelocityemail.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmailExecutor @Autowired
constructor(private val emailService: EmailService) : RabbitExecutor<String?> {

    internal var logger = LoggerFactory.getLogger(EmailExecutor::class.java)

    @Throws(Exception::class)
    override fun execute(messageId: String?) {
        emailService.send(messageId)
        logger.info(" [x] Execute for email notification with message id : {}", messageId)

    }
}
