package com.keccikun.email.kotlinvelocityemail.domain

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "email_messages")
class EmailMessage(@field:Field("to") var to: ArrayList<String>?,
                   @field:Field("cc") var cc: ArrayList<String>?,
                   @field:Field("bcc") var bcc: ArrayList<String>?,
                   @field:Field("reply_to") var replyTo: String?,
                   @field:Field("subject") var subject: String?,
                   @field:Field("template_filename") var templateFilename: String?,
                   @field:Field("payload") var payload: String?,
                   @field:Field("status") var status: Int?,
                   @field:Field("response") var response: String?) : Base() {

    init {
        this.created_at = Date()
        this.updated_at = Date()
    }
}