package com.example.citylisteningto_nadifi.adapter

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.viewholder.SongViewHolder
import com.example.citylisteningto_nadifi.activities.DatabaseView
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.viewmodels.SongViewModel
import kotlinx.coroutines.launch
import okio.IOException

class SongAdapter (private val songViewModel: SongViewModel, var songs: List<Song>?, val songDao: SongDao, val mp : MediaPlayer, private val context: Context, private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<SongViewHolder>() {

    var SongPlaying : Int = -1
    var path : String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.song_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs?.get(position)?.id!!

        holder.apply {
            tvSongTitle.text = "Audio ${songs?.get(position)?.id}"
            if(songs?.get(position)?.hidden == true){
                buttonHide.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_visibility_24, 0, 0)
            } else{
                buttonHide.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_visibility_off_24, 0, 0)
            }
            if(!(songViewModel.isNetworkAvailable(context)) || context is DatabaseView ){
                buttonHide.isVisible = false
                buttonDelete.isVisible = false
            }

            if(song == SongPlaying){
                holder.buttonSong.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_pause_24, 0, 0)
            } else{
                holder.buttonSong.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_play_arrow_24, 0, 0)

            }
        }

        holder.buttonHide.setOnClickListener{
            if(songs?.get(position)?.hidden == true) {
                songViewModel.showSong(song, songDao)
            } else{
                songViewModel.hideSong(song, songDao)
            }
        }

        holder.buttonDelete.setOnClickListener{
            if( songs?.get(position)?.id!! >0) {
                songViewModel.deleteSong(song, songDao)
            }
        }

        holder.buttonInfo.setOnClickListener{
            if(!songViewModel.isNetworkAvailable(context) || context is DatabaseView){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Dettagli Canzone")
                builder.setMessage("Audio: ${songs?.get(position)?.id}\n \nLatitudine: ${songs?.get(position)?.latitude}\n \nLongitudine: ${songs?.get(position)?.longitude}\n" +
                        "\nLoudness: ${"%.2f".format(songs?.get(position)?.loudness)}\n" + "\nBPM: ${songs?.get(position)?.bpm}\n" +  "\nMood: \n${songs?.get(position)?.mood.toString()}\n" +
                        "\nGenre: \n${songs?.get(position)?.genre.toString()}\n" +  "\nInstrument: \n${songs?.get(position)?.instrument.toString()}")
                builder.setPositiveButton("OK", null)
                builder.show()
            } else{
                songViewModel.getSongInfo(song)
            }
        }

        mp.setOnCompletionListener {
            mp.reset()
            SongPlaying = -1
            notifyDataSetChanged()
        }

        holder.buttonSong.setOnClickListener{
            lifecycleOwner.lifecycleScope.launch {
                playSong(holder, song)
            }
        }
    }


    suspend fun playSong(holder: SongViewHolder, songId: Int?) {
        val pathPlaying = path
        path =songDao.getSongPath(songId!!)

        if(path != null){

            try{
                if(mp.isPlaying && path == pathPlaying){
                    mp.pause()
                    mp.reset()
                    SongPlaying = -1
                } else {
                    if(mp.isPlaying){
                        mp.pause()
                        mp.reset()

                    }
                    mp.setDataSource(path)
                    mp.prepare()
                    mp.start()
                    SongPlaying = songId

                }
                notifyDataSetChanged()
            } catch (e : IOException){
            }
        }
        else{
            Toast.makeText(context, "Assicurati che l'audio sia presente nel dispositivo!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return songs?.size ?: 0
    }


}