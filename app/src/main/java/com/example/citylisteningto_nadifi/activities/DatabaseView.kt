package com.example.citylisteningto_nadifi.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.adapter.SongAdapter
import com.example.citylisteningto_nadifi.db.SongDatabase
import com.example.citylisteningto_nadifi.viewmodels.SongViewModel
import kotlinx.coroutines.launch

class DatabaseView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_database_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerDb: RecyclerView = findViewById(R.id.RecyclerDb)
        val songViewModel: SongViewModel by viewModels()
        val mp = MediaPlayer()
        val songDao = SongDatabase.getInstance(this).songDao()
        val dbAdapter = SongAdapter(songViewModel, listOf(), songDao,mp,this, this)

        recyclerDb.adapter = dbAdapter
        recyclerDb.layoutManager = LinearLayoutManager(this)

        songViewModel.songs.observe(this) { newdata ->
            dbAdapter.apply {
                songs = newdata
                notifyDataSetChanged()
                Log.d("SIZE", itemCount.toString())
            }

        }

        lifecycleScope.launch {
            songViewModel.getLocalSongs(songDao)
        }





    }
}