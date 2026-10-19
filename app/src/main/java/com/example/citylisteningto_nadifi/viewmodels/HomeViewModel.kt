package com.example.citylisteningto_nadifi.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citylisteningto_nadifi.api.ApiService
import com.example.citylisteningto_nadifi.api.RetrofitClientInstance
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.classes.SongImport
import com.example.citylisteningto_nadifi.classes.SongResponse
import com.example.citylisteningto_nadifi.db.SongImportDao
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback


class HomeViewModel: ViewModel() {
    val statusCode = MutableLiveData<Int>()
    private val _songsImported = MutableLiveData<List<SongImport>>()
    val songsImported: LiveData<List<SongImport>> get() = _songsImported
    private val lastSongIdMutex = Mutex()


    fun uploadAudio(filePath: String, longitude: Double, latitude: Double, songDao: SongDao, context: Context) {
        val file = File(filePath)
        val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val gson = Gson()

        val service: ApiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<ResponseBody> = service.uploadAudio(longitude, latitude, body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                statusCode.value = response.code()
                if (response.isSuccessful) {
                    val rawResponse = response.body()?.string()
                    if (rawResponse != null) {
                        try {
                            val songUploaded = gson.fromJson(rawResponse, SongResponse::class.java)
                            val mood = songUploaded.mood.toString()
                            val bpm = songUploaded.bpm
                            val loudness = songUploaded.loudness
                            val genre = songUploaded.genre.toString()
                            val instrument = songUploaded.instrument.toString()
                            val sharedPreferences: SharedPreferences =
                                context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                            val client_id = sharedPreferences.getInt("client_id", 0 )

                            viewModelScope.launch {
                                val lastId = getLastSongId()
                                if (lastId != null) {
                                    songDao.insertSong(Song(lastId, longitude, latitude, false,bpm, loudness, mood, genre, instrument, filePath, client_id))
                                }
                            }
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
                statusCode.value = 0
            }
        })
    }


    suspend fun getLastSongId(): Int? {
        return withContext(Dispatchers.IO) {
            lastSongIdMutex.withLock {
                val service: ApiService = RetrofitClientInstance.getRetrofitInstance().create(
                    ApiService::class.java)
                val response = service.getMySongs().execute()

                if (response.isSuccessful) {
                     response.body()?.last()?.id
                } else {
                    Log.e("API_ERROR", "Errore nella chiamata: ${response.code()}")
                     null
                }
        }   }
    }

    suspend fun getSongUpped(songImportDao: SongImportDao, client_id: Int){
        _songsImported.postValue(songImportDao.getUserSongs(client_id))
    }
}

