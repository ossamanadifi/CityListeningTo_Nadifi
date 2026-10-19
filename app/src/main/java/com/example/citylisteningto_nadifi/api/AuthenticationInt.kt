package com.example.citylisteningto_nadifi.api

import okhttp3.Interceptor
import okhttp3.Response

    class AuthenticationInt(val token: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

            return chain.proceed(newRequest)
        }
    }
