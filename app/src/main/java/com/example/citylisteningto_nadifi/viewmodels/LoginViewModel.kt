package com.example.citylisteningto_nadifi.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citylisteningto_nadifi.api.ApiService
import com.example.citylisteningto_nadifi.api.RetrofitClientInstance
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.classes.LoginResponse
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    val statusCode = MutableLiveData<Int>()

    fun login(username: String, password: String, context: Context){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)

        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<LoginResponse> = service.login(username, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                statusCode.value = response.code()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        editor.putInt("client_id", loginResponse.clientId)

                        editor.apply()
                        Log.e("API", "Richiesta andata a buon fine! $loginResponse")
                        val newRetrofit =
                            RetrofitClientInstance.getRetrofitInstance(loginResponse.clientSecret)
                    }
                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
                statusCode.value = 10
            }
        })
    }


    fun deleteAccount(id: Int, songDao: SongDao){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.deleteAccount()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        songDao.deleteSongsById(id)
                    }
                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
                statusCode.value = 10
            }
        })

    }
}

