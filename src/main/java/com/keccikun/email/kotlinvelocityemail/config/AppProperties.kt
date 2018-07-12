package com.keccikun.email.kotlinvelocityemail.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
open class AppProperties {

    var rabbit: Rabbit? = null

    var firebase: Firebase? = null

    var infobip: Infobip? = null

    var smtp: Smtp? = null

    class Rabbit {
        var url: String? = null
        var username: String? = null
        var password: String? = null
    }

    class Firebase {
        var firebaseUrl: String? = null
        var apiKey: String? = null
    }

    class Infobip {
        var authorization: String? = null
        var smsUrl: String? = null
        var from: String? = null
        var basicAuthorization: String? = null
        var otpUrl: String? = null
        var resendOtpUrl: String? = null
        var applicationId: String? = null
        var messageId: String? = null
        var verifyUrl: String? = null
    }

    class Smtp {
        var host: String? = null
        var user: String? = null
        var name: String? = null
        var password: String? = null
        var templateSource: String? = null
        var templateExtension: String? = null
    }
}