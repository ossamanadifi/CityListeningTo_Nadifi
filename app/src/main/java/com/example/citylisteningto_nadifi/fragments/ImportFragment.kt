package com.example.citylisteningto_nadifi.fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.adapter.ImportAdapter
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.db.SongDatabase
import com.example.citylisteningto_nadifi.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

class ImportFragment : Fragment() {
    private lateinit var  mp : MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_import, container, false)

        val homeViewModel: HomeViewModel by viewModels()
        val songImportDao = SongDatabase.getInstance(requireContext()).songImportDao()
        val songDao = SongDatabase.getInstance(requireContext()).songDao()
        mp = MediaPlayer()
        val importAdapter = ImportAdapter(homeViewModel, listOf(), songImportDao, songDao, mp , requireContext(), viewLifecycleOwner)
        val recyclerImport: RecyclerView = rootView.findViewById(R.id.recyclerImport)

        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val client_id = sharedPreferences.getInt("client_id", 0)

        recyclerImport.adapter = importAdapter
        recyclerImport.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.songsImported.observe(viewLifecycleOwner) { newdata ->
            importAdapter.apply {
                songs = newdata
                notifyDataSetChanged()
                Log.d("SIZE", itemCount.toString())
            }

        }
        lifecycleScope.launch {
            homeViewModel.getSongUpped(songImportDao, client_id)
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


