package com.keccikun.email.kotlinvelocityemail.api.controller

import com.keccikun.email.kotlinvelocityemail.api.request.EmailRequest
import com.keccikun.email.kotlinvelocityemail.service.EmailService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/email/*")
class EmailController(private val emailService: EmailService) {

    @RequestMapping("/send")
    @Throws(Exception::class)
    internal fun sendEmail(@RequestBody request :EmailRequest): ResponseEntity<Any?> {
        emailService.publish(request)
        return ResponseEntity(HttpStatus.OK)
    }
}