package com.czf.student.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetWork {
    private val longClient=OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val shortClient=OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun login(type:String,userName:String,password:String):Int {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("userName", userName)
                .add("password", password)
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/$type/login")
                .post(formBody)
                .build()

            try {
                val response: Response = shortClient.newCall(request).execute()
                response.code
            }
            catch (e:IOException){
                404
            }
        }
    }
}