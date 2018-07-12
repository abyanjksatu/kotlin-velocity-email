package com.keccikun.email.kotlinvelocityemail.rabbitmq.executor

interface RabbitExecutor<T> {
    @Throws(Exception::class)
    fun execute(t: T)
}