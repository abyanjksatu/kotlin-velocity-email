package com.keccikun.email.kotlinvelocityemail.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.format.annotation.DateTimeFormat
import java.util.*


abstract class Base {

    @Id
    var id: String? = null

    @Field("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var created_at: Date? = null

    @Field("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var updated_at: Date? = null


    protected constructor() {}

    constructor(id: String) {
        this.id = id
    }
}
