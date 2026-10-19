package com.example.citylisteningto_nadifi.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citylisteningto_nadifi.api.ApiService
import com.example.citylisteningto_nadifi.api.RetrofitClientInstance
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.classes.SongMapped
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongViewModel() : ViewModel() {
    var deleteStatusCode = MutableLiveData<Int>()
    val showStatusCode = MutableLiveData<Int>()
    val hideStatusCode = MutableLiveData<Int>()
    val infoStatusCode = MutableLiveData<Int>()
    var songRequested = MutableLiveData<SongMapped>()
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs
    private val _songsAll = MutableLiveData<List<Song>>()
    val songsAll: LiveData<List<Song>> get() = _songsAll

    fun getMySongs(){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
            val call: Call<List<Song>> = service.getMySongs()
        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    _songs.postValue(response.body())
                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })
    }

    fun getAllSongs(){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<List<Song>> = service.getAllSongs()
        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (response.isSuccessful) {
                    _songsAll.postValue(response.body())
                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })
    }

    fun getSongInfo(id: Int){
        val gson = Gson()
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.getSongInfo(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                infoStatusCode.value = response.code()
                if (response.isSuccessful) {
                    val rawResponse = response.body()?.string()
                    if (rawResponse != null) {
                        try {
                            val songUploaded = gson.fromJson(rawResponse, SongMapped::class.java)
                            val songMapped: SongMapped = SongMapped(songUploaded.longitude, songUploaded.latitude, songUploaded.id, songUploaded.creator_id,songUploaded.creator_username, songUploaded.tags)

                            songRequested.postValue(songMapped)
                        } catch (e: JsonSyntaxException) {
                            Log.e("Parsing Error", "Errore nel parsing del JSON: ${e.message}")
                        }
                    } else {
                        Log.e("Parsing Error", "${rawResponse}")
                    }
                } else {
                    Log.e("API_ERROR", "Errore nella chiamata: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })

    }

    fun hideSong(id: Int, songDao: SongDao){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.hideSong(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                hideStatusCode.value = response.code()
                if (response.isSuccessful) {
                    Log.e("BUTTON_CLICK", "HIDE andato a buon fine!")
                    viewModelScope.launch{
                        songDao.UpdateSongVisible(id)
                    }
                    getMySongs()

                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })
    }

    fun showSong(id: Int?, songDao: SongDao){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.showSong(id!!)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showStatusCode.value = response.code()
                if (response.isSuccessful) {
                    viewModelScope.launch{
                        songDao.UpdateSongVisible(id)
                    }
                    getMySongs()
                } else {
                    Log.e("API", "Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })

    }

    fun deleteSong(id: Int, songDao: SongDao){
        val service: ApiService =
            RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.deleteSong(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                deleteStatusCode.value = response.code()
                if (response.isSuccessful) {
                    viewModelScope.launch{
                        if(songDao.getSongById(id) != null){
                            songDao.deleteSong(songDao.getSongById(id)!!)
                        }
                    }
                    getMySongs()

                } else {
                    Log.e("API", "$id Errore: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Errore nella chiamata: ${t.message}", t)
            }
        })

    }

    suspend fun getLocalSongs(songDao: SongDao, ){
            _songs.postValue(songDao.getAllSongs())
    }

    suspend fun getMyLocalSongs(songDao: SongDao, context: Context){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val client_id = sharedPreferences.getInt("client_id", 0 )
        _songs.postValue(songDao.getMySongs(client_id))
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = connectivityManager?.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
