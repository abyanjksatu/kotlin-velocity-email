package com.keccikun.email.kotlinvelocityemail.service

import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.concurrent.TimeUnit


@Service("RestService")
open class RestService {

    val okHttpClient: OkHttpClient
    val gson: Gson
    lateinit var listener: RestCallbackListener
    init {

        this.gson = Gson()

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        this.okHttpClient = OkHttpClient.Builder().connectionPool(ConnectionPool(4,
                10000, TimeUnit.SECONDS))
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .build()
    }

    internal var logger = LoggerFactory.getLogger(RestService::class.java)

    fun executePost(url: String, json: String, apiKey: String,restCallbackListener: RestCallbackListener) {
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json; UTF-8")
                .addHeader("Authorization", apiKey)
                .build()
        logger.info("[x] json request " + json)
        try {
            this.okHttpClient.newCall(request).execute().use({ response ->
                if (response.isSuccessful()) {
                    var result  = response.body()?.string()
                    logger.info("[x] json response " +  result)
                    restCallbackListener.onSuccess(result)
                } else {
                    logger.info("[x] error "+ response.code())
                    restCallbackListener.onError(response.code())
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        val HTTPSuccess = 200
    }

    interface RestCallbackListener{
        fun onSuccess(string: String?)
        fun onError(code: Int)
    }
}
