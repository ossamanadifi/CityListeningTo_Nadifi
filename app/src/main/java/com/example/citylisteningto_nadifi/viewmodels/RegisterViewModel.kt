package com.example.citylisteningto_nadifi.viewmodels
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citylisteningto_nadifi.api.ApiService
import com.example.citylisteningto_nadifi.classes.RegisterRequest
import com.example.citylisteningto_nadifi.classes.RegisterResponse
import com.example.citylisteningto_nadifi.api.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    val statusCode = MutableLiveData<Int>()
    val errorMessage = MutableLiveData<String>()

    fun register(username: String, password: String){
        val registerRequest = RegisterRequest(username, password)

        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<RegisterResponse> = service.register(registerRequest)
        call.enqueue(object : Callback<RegisterResponse> {

            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    statusCode.value = response.code()
                    Log.e("API", "Richiesta andata a buon fine!")

                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
                statusCode.value = 10
            }
        })
    }
}
