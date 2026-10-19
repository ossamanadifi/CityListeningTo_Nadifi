package com.example.citylisteningto_nadifi.classes

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("client_secret") val clientSecret: String
)





