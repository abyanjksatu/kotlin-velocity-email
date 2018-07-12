package com.keccikun.email.kotlinvelocityemail.api.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class EmailRequest constructor(): Serializable {

    @JsonProperty("to")
    var to: ArrayList<String>? = null

    @JsonProperty("cc")
    var cc: ArrayList<String>? = null

    @JsonProperty("bcc")
    var bcc: ArrayList<String>? = null

    @JsonProperty("reply_to")
    var replyTo: String? = null

    @JsonProperty("subject")
    var subject: String? = null

    @JsonProperty("template_filename")
    var templateFilename: String? = null

    @JsonProperty("payload")
    var payload: Map<String, String>? = null
}