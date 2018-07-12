package com.keccikun.email.kotlinvelocityemail.service

import com.keccikun.email.kotlinvelocityemail.api.request.EmailRequest
import com.keccikun.email.kotlinvelocityemail.config.AppProperties
import com.keccikun.email.kotlinvelocityemail.domain.EmailMessage
import com.keccikun.email.kotlinvelocityemail.rabbitmq.publisher.EmailPublisher
import com.keccikun.email.kotlinvelocityemail.repositories.EmailRepository
import com.sun.mail.smtp.SMTPTransport
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.util.regex.Pattern
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service("EmailService")
class EmailService @Autowired constructor(var emailPublisher: EmailPublisher,
                                          var restService : RestService,
                                          var emailRepository: EmailRepository,
                                          var appProperties: AppProperties) {

    internal var logger = LoggerFactory.getLogger(EmailService::class.java)

    companion object {
        val EXCHANGE_NAME = "email_exchange"

        val EMAIL_ROUTE_KEY = "email_route_key"
        val EMAIL_QUEUE = "email_queue"
    }

    fun publish(request: EmailRequest) {
        emailPublisher.publish(restService.gson.toJson(request))
    }

    fun send(emailRequestJson : String?){
        val emailRequest = restService.gson.fromJson(emailRequestJson, EmailRequest::class.java)

        if (isEmailNotificationValid(emailRequest)){
        val emailMessage = emailRepository.save(EmailMessage(
                emailRequest.to,
                emailRequest.cc,
                emailRequest.bcc,
                emailRequest.replyTo,
                emailRequest.subject,
                emailRequest.templateFilename,
                emailRequest.payload.toString(),
                null,
                null
                ))

            val html = SetTemplateEmail(emailMessage.templateFilename, emailRequest.payload)

            val response = SendSMTPMessage(emailMessage.subject,
                    emailMessage.to,
                    emailMessage.cc,
                    emailMessage.bcc,
                    emailMessage.replyTo,
                    html)

            emailMessage.response = response

            emailRepository.save(emailMessage)

        } else {
            logger.info("INVALID Email Notification Request: {}", emailRequestJson)
        }

    }

    fun SendSMTPMessage(subject: String?,
                        to: ArrayList<String>?,
                        cc: ArrayList<String>?,
                        bcc: ArrayList<String>?,
                        replyTo: String?,
                        payload: String) : String {

        val properties = System.getProperties()

        properties["mail.smtps.host"] = appProperties.smtp?.host
        properties["mail.smtps.auth"] = "true"

        val session = Session.getDefaultInstance(properties, null)

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(appProperties.smtp?.user, appProperties.smtp?.name))

        to?.forEach {
            message.addRecipient(Message.RecipientType.TO, InternetAddress(it))
        }

        if (cc != null){
            cc?.forEach {
                if(isEmailValid(it)) {
                    message.addRecipient(Message.RecipientType.CC, InternetAddress(it))
                }
            }
        }

        if (bcc != null){
            bcc?.forEach {
                if(isEmailValid(it)) {
                    message.addRecipient(Message.RecipientType.BCC, InternetAddress(it))
                }
            }
        }

        if (replyTo != null){
            if(isEmailValid(replyTo)) {
                message.replyTo = arrayOf<Address>(InternetAddress(replyTo))
            }
        }
        message.subject = subject
        message.setContent(payload, "text/html")

        val transport = session.getTransport("smtps") as SMTPTransport
        transport.connect(
                appProperties.smtp?.host,
                appProperties.smtp?.user,
                appProperties.smtp?.password
        )

        transport.sendMessage(message, message.allRecipients)

        val response = transport.lastServerResponse

        logger.info("Response: {}", response)

        transport.close()

        return response
    }

    fun SetTemplateEmail(templateFilename: String?, payload: Map<String, String>?): String {
        val velocityEngine = VelocityEngine()
        velocityEngine.init()

        val template : Template = velocityEngine.getTemplate(appProperties.smtp?.templateSource + templateFilename + appProperties.smtp?.templateExtension)

        val velocityContext = VelocityContext()
        val mapRequest= payload

        val set = mapRequest?.entries
        val iterator = set?.iterator()

        while (iterator!!.hasNext()) {
            val payloadEntry = iterator.next()
            velocityContext.put(payloadEntry.key, payloadEntry.value);
        }

        val stringWriter = StringWriter()
        template.merge(velocityContext, stringWriter)

        return stringWriter.toString()
    }

    fun isEmailValid(email: String?): Boolean {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun isEmailNotificationValid(emailNotificationRequest: EmailRequest): Boolean {

        var toValidate = true

        emailNotificationRequest.to?.forEach{
            toValidate = isEmailValid(it) && toValidate
        }

        return emailNotificationRequest != null
                && emailNotificationRequest.to != null && toValidate
                && emailNotificationRequest.subject != null && emailNotificationRequest.subject != ""
                && emailNotificationRequest.templateFilename != null && emailNotificationRequest.templateFilename != ""
                && emailNotificationRequest.payload != null
    }
}