package com.example.citylisteningto_nadifi.adapter

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.viewholder.ImportViewHolder
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.db.SongDao
import com.example.citylisteningto_nadifi.classes.SongImport
import com.example.citylisteningto_nadifi.db.SongImportDao
import com.example.citylisteningto_nadifi.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import okio.IOException

class ImportAdapter (private val homeViewModel: HomeViewModel, var songs: List<SongImport>?, val songImportDao: SongImportDao, val songDao: SongDao, val mp : MediaPlayer, private val context: Context, private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<ImportViewHolder>() {

    var SongPlaying : Int = -1
    var path : String? = null
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val client_id = sharedPreferences.getInt("client_id", 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportViewHolder {
        return ImportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.import_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImportViewHolder, position: Int) {
        val song = songs?.get(position)?.id!!

        holder.apply {
            tvSongTitle.text = "Audio ${songs?.get(position)?.id}"
            if(song == SongPlaying){
                holder.buttonSong.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_pause_24, 0, 0)
            } else{
                holder.buttonSong.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.baseline_play_arrow_24, 0, 0)

            }

            holder.buttonImport.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.baseline_upload_24, 0, 0)
        }

        holder.buttonImport.setOnClickListener{
            homeViewModel.uploadAudio(songs?.get(position)?.path!!, songs?.get(position)?.longitude!!, songs?.get(position)?.latitude!!, songDao, context )
            lifecycleOwner.lifecycleScope.launch {
                val songUpped = songImportDao.getSongById(songs!!.get(position).id)
                songImportDao.deleteSong(songUpped)
                homeViewModel.getSongUpped(songImportDao,client_id)
            }
            notifyDataSetChanged()

        }

        holder.buttonDelete.setOnClickListener{

            if( songs?.get(position)?.id!! >0) {
                lifecycleOwner.lifecycleScope.launch {
                    songImportDao.deleteSong(songs?.get(position)!!)
                    homeViewModel.getSongUpped(songImportDao,client_id )
                }
                notifyItemRemoved(position)


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


    suspend fun playSong(holder: ImportViewHolder, songId: Int) {
        val pathPlaying = path
        path = songImportDao.getSongPath(songId!!)

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