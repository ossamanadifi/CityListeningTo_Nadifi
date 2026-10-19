package com.example.citylisteningto_nadifi.fragments

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.adapter.SongAdapter
import com.example.citylisteningto_nadifi.db.SongDatabase
import com.example.citylisteningto_nadifi.activities.LoginView
import com.example.citylisteningto_nadifi.viewmodels.SongViewModel
import kotlinx.coroutines.launch

class SongFragment : Fragment() {

    private lateinit var mp: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_song, container, false)

        val songViewModel: SongViewModel by viewModels()
        val songDao = SongDatabase.getInstance(requireContext()).songDao()
        mp = MediaPlayer()
        val songAdapter = SongAdapter(songViewModel, listOf(), songDao,mp , requireContext(), viewLifecycleOwner)
        val recyclerSong: RecyclerView = rootView.findViewById(R.id.recyclerSong)

        recyclerSong.adapter = songAdapter
        recyclerSong.layoutManager = LinearLayoutManager(requireContext())

        songViewModel.deleteStatusCode.observe(viewLifecycleOwner){ code ->
            if(code == 200){
                Toast.makeText(requireContext(), "Delete andato a buon fine!", Toast.LENGTH_SHORT).show()
            } else if(code == 404) {
                Toast.makeText(requireContext(), "L'audio deve essere visibile prima di essere cancellato!", Toast.LENGTH_SHORT).show()
            } else if(code == 401) {
                Toast.makeText(requireContext(), "Autenticazione scaduta. Effettuare l'accesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginView::class.java))
                requireActivity().finish()
            }
        }

        songViewModel.hideStatusCode.observe(viewLifecycleOwner){ code ->
            if(code == 200){
                Toast.makeText(requireContext(), "Hide andato a buon fine!", Toast.LENGTH_SHORT).show()
            } else if(code == 401) {
                Toast.makeText(requireContext(), "Autenticazione scaduta. Effettuare l'accesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginView::class.java))
                requireActivity().finish()
            }
        }

        songViewModel.showStatusCode.observe(viewLifecycleOwner){ code ->
            if(code == 200){
                Toast.makeText(requireContext(), "Show andato a buon fine!", Toast.LENGTH_SHORT).show()
            } else if(code == 401) {
                Toast.makeText(requireContext(), "Autenticazione scaduta. Effettuare l'accesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginView::class.java))
                requireActivity().finish()
            }
        }

        songViewModel.infoStatusCode.observe(viewLifecycleOwner){ code ->
            if(code == 404){
                Toast.makeText(requireContext(), "L'audio deve essere visibile!", Toast.LENGTH_SHORT).show()
            } else if(code == 401) {
                Toast.makeText(requireContext(), "Autenticazione scaduta. Effettuare l'accesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginView::class.java))
                requireActivity().finish()
            }
        }

        songViewModel.songRequested.observe(viewLifecycleOwner){ songClicked->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Dettagli Canzone")
            builder.setMessage("Audio: ${songClicked.id}\n \nLatitudine: ${songClicked.latitude}\n \nLongitudine: ${songClicked.longitude}\n \n${songClicked.tags.toString()}")

            builder.setPositiveButton("OK", null)
            builder.show()
        }

        songViewModel.songs.observe(viewLifecycleOwner) { newdata ->
            songAdapter.apply {
                songs = newdata
                notifyDataSetChanged()
                Log.d("SIZE", itemCount.toString())
            }

        }

        if (songViewModel.isNetworkAvailable(requireContext())) {
            songViewModel.getMySongs()
        } else{
            lifecycleScope.launch {
                songViewModel.getMyLocalSongs(songDao, requireContext())
            }
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mp.isInitialized) {
            mp.release()
        }
    }






}


