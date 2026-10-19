package com.example.citylisteningto_nadifi.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.api.ApiService
import com.example.citylisteningto_nadifi.api.RetrofitClientInstance
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.db.SongDatabase
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.classes.SongImport
import com.example.citylisteningto_nadifi.classes.SongResponse
import com.example.citylisteningto_nadifi.db.SongImportDao
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MyWorker(context:Context, workerParameter: WorkerParameters) : Worker(context, workerParameter) {
    val lastSongIdMutex = Mutex()
    override fun doWork(): Result {
        uploadSongs(applicationContext)

       return Result.success()
    }

    private fun uploadSongs(context: Context) {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val checkedToken = checkToken()
        Log.d("SongUpload", "$checkedToken token")

        val client_id = sharedPreferences.getInt("client_id", 0)
        var songCounter = sharedPreferences.getInt("songCounter", 0)
        var notiCheck = songCounter;
        val songDao = SongDatabase.getInstance(context).songDao()
        val songImportDao = SongDatabase.getInstance(context).songImportDao()
        var c=0
        while(sharedPreferences.contains("Song$c")){
            Log.d("SongUploading", "Song$c")
            val songUpped = Gson().fromJson(sharedPreferences.getString("Song$c", null), SongImport::class.java)


            if(songUpped.client_id == client_id && checkedToken) {
                CoroutineScope(Dispatchers.IO).launch {
                    lastSongIdMutex.withLock {
                        uploadAudio(
                            songUpped.path,
                            songUpped.latitude,
                            songUpped.longitude,
                            songDao,
                            client_id
                        )
                    }
                }
            } else{
                CoroutineScope(Dispatchers.IO).launch {
                    importLater(SongImport(0, songUpped.latitude, songUpped.longitude, songUpped.path, songUpped.client_id!!), songImportDao)
                }
            }
            editor.remove("Song$c")
            editor.commit()
            c++
            songCounter--
        }

        editor.putInt("songCounter", songCounter)
        editor.commit()
        if(notiCheck>0){
            sendNotification()
        }
    }

    private suspend fun importLater(songImport: SongImport, songImportDao: SongImportDao) {
        songImportDao.insertSong(songImport)
    }

    private fun checkToken(): Boolean {
        val service: ApiService = RetrofitClientInstance.getRetrofitInstance()
            .create(ApiService::class.java)
        val response = service.getMySongs().execute()
        if (response.isSuccessful) {
            val rawResponse = response.body().toString()
            if (rawResponse != null) {
                val responseCode = response.code()
                if (responseCode == 401){
                    return false
                }

                if(responseCode == 200){
                    return true
                }
            }
        } else {
            Log.e("API_ERROR", "Failed API call: ${response.code()} - ${response.errorBody()?.string()}")
        }

        return false
    }

    suspend fun uploadAudio(filePath: String, longitude: Double, latitude: Double, songDao: SongDao, client_int: Int) {
        try {
            val file = File(filePath)
            val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val gson = Gson()

            val service: ApiService = RetrofitClientInstance.getRetrofitInstance()
                .create(ApiService::class.java)
            val response = service.uploadAudio(longitude, latitude, body).execute()

            if (response.isSuccessful) {
                val rawResponse = response.body()?.string()
                if (rawResponse != null) {
                    val songUploaded = gson.fromJson(rawResponse, SongResponse::class.java)
                    val mood = songUploaded.mood.toString()
                    val bpm = songUploaded.bpm
                    val loudness = songUploaded.loudness
                    val genre = songUploaded.genre.toString()
                    val instrument = songUploaded.instrument.toString()
                    val lastId = getLastSongId()
                    if (lastId != null) {
                        songDao.insertSong(Song(lastId, longitude, latitude, false, bpm, loudness, mood, genre, instrument, filePath, client_int))
                        Log.d("SongUpload", "Song uploaded: ${songUploaded.danceability}")
                    }

                }
            } else {
                Log.e("API_ERROR", "Failed API call: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("UPLOAD_ERROR", "Error uploading song: ${e.message}", e)
        }
    }

     fun getLastSongId(): Int? {
        val service: ApiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val response = service.getMySongs().execute()

        if (response.isSuccessful) {
            return response.body()?.maxByOrNull { it.id }?.id
        } else {
            Log.e("API_ERROR", "Error during API call: ${response.code()}")
            return null
        }
    }

    private fun sendNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("uploadchannel", "UploadChannel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "uploadchannel")
            .setContentTitle("Sincronizzazione completata.")
            .setContentText("Servizio sincronizzato con i upload posticipati.")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }


}