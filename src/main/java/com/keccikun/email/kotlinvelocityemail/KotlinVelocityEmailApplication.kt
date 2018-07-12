package com.keccikun.email.kotlinvelocityemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories("com.keccikun.email.repositories")
open class KotlinVelocityEmailApplication {

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			SpringApplication.run(KotlinVelocityEmailApplication::class.java, *args)
		}
	}
}