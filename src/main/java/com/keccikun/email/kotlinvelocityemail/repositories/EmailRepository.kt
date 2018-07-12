package com.keccikun.email.kotlinvelocityemail.repositories

import com.keccikun.email.kotlinvelocityemail.domain.EmailMessage
import org.springframework.data.mongodb.repository.MongoRepository

interface EmailRepository : MongoRepository<EmailMessage, String> {

}