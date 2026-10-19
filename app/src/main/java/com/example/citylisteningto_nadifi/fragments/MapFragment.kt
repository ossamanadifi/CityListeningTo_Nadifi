package com.example.citylisteningto_nadifi.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.classes.Song
import com.example.citylisteningto_nadifi.classes.SongMapped
import com.example.citylisteningto_nadifi.viewmodels.SongViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private val songViewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        val startPoint = GeoPoint(41.0, 12.0)
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(7)

        songViewModel.songRequested.observe(viewLifecycleOwner) { songClicked ->
            songDetails(songClicked)
        }

        songViewModel.songsAll.observe(viewLifecycleOwner) { songs ->
            // Cancella i marker precedenti
            loadSongs(songs)

        }

        songViewModel.getAllSongs()
        return rootView
    }

    private fun songDetails(songClicked: SongMapped){
        val builder = AlertDialog.Builder(requireContext())
        //builder.setTitle("Dettagli Canzone")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_song, null)

        builder.setView(dialogLayout)

        val songIdTextView: TextView = dialogLayout.findViewById(R.id.songIdTextView)
        val songDetailsTextView: TextView = dialogLayout.findViewById(R.id.txtLatLong)
        val songCreator: TextView = dialogLayout.findViewById(R.id.txtCreator)
        val songTags: TextView = dialogLayout.findViewById(R.id.txtTags)

        songIdTextView.text = "Traccia: ${songClicked.id}"
        songDetailsTextView.text = "Latitudine: ${songClicked.latitude}\nLongitudine: ${songClicked.longitude}\n"
        songCreator.text = "Username creatore: ${songClicked.creator_username}"
        songTags.text = "${songClicked.tags}"

        builder.setPositiveButton("OK", null)

        builder.show()
    }

    private fun loadSongs(songs : List<Song>){
        mapView.overlays.clear()

        songs.forEach { song ->
            if (song.latitude in -85.05112877980658..85.05112877980658 &&
                song.longitude in -180.0..180.0
            ) {
                val marker = Marker(mapView)
                marker.position = GeoPoint(song.latitude, song.longitude)
                marker.title = song.id.toString()

                marker.setOnMarkerClickListener { clickedMarker, mapView ->
                    songViewModel.getSongInfo(clickedMarker.title.toInt())
                    true
                }
                mapView.overlays.add(marker)
            }
        }

    }

    }