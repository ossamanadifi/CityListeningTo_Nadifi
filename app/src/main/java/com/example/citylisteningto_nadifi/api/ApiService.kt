package com.example.citylisteningto_nadifi.api

import com.example.citylisteningto_nadifi.classes.LoginResponse
import com.example.citylisteningto_nadifi.classes.RegisterRequest
import com.example.citylisteningto_nadifi.classes.RegisterResponse
import com.example.citylisteningto_nadifi.classes.Song
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/lam2024/auth/token")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    @POST("/lam2024/auth")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("/lam2024/audio/all")
    fun getAllSongs() : Call<List<Song>>

    @GET("/lam2024/audio/{id}")
    fun getSongInfo(@Path("id") songId: Int): Call<ResponseBody>

    @GET("/lam2024/audio/my")
    fun getMySongs() : Call<List<Song>>

    @GET("/lam2024/audio/my/{id}/hide")
    fun hideSong(@Path("id") songId: Int): Call<ResponseBody>

    @GET("/lam2024/audio/my/{id}/show")
    fun showSong(@Path("id") songId: Int): Call<ResponseBody>

    @DELETE("/lam2024/audio/{id}")
    fun deleteSong(@Path("id") songId: Int): Call<ResponseBody>

    @DELETE("/lam2024/auth/unsubscribe")
    fun deleteAccount(): Call<ResponseBody>

    @Multipart
    @POST("/lam2024/upload")
    fun uploadAudio(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}