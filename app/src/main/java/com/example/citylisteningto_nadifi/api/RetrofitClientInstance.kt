

package com.example.citylisteningto_nadifi.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientInstance {
    companion object {
        private var retrofit: Retrofit? = null
        private var token: String? = null

        const val BASE_URL = "http://130.136.2.83/"

        fun getRetrofitInstance(newToken: String? = null): Retrofit {
            if (newToken != null) {
                token = newToken
                retrofit = null
            }
            if (retrofit == null) {
                val client = OkHttpClient.Builder()
                    .apply {
                        token?.let {
                            addInterceptor(AuthenticationInt(it))
                        }
                    }
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}

