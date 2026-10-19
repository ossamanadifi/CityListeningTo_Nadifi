package com.example.citylisteningto_nadifi.fragments

import com.example.citylisteningto_nadifi.viewmodels.HomeViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.properties.Delegates
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.db.SongDatabase
import com.example.citylisteningto_nadifi.activities.LoginView
import com.example.citylisteningto_nadifi.classes.SongImport
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class HomeFragment : Fragment() {
    private lateinit var buttonRecord: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var buttonStop: Button
    private lateinit var buttonPlay: Button
    private lateinit var buttonImport: Button
    private lateinit var buttonImportLater: Button
    private lateinit var buttonImportPost : FloatingActionButton
    private lateinit var mediaRec: MediaRecorder
    private lateinit var path : String
    private var uploadFlag: Int? = null
    private var longitude by Delegates.notNull<Double>()
    private var latitude by Delegates.notNull<Double>()
    private lateinit var mp : MediaPlayer

    private var startTime: Long = 0
    private lateinit var textView: TextView
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Home"
        val actionBarColor = ContextCompat.getColor(requireContext(), R.color.lightgrey)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setBackgroundDrawable(
            ColorDrawable(actionBarColor)
        )

        buttonImportPost = rootView.findViewById(R.id.btnImportPost)
        buttonRecord = rootView.findViewById(R.id.btnRecord)
        buttonImport = rootView.findViewById(R.id.btnImport)
        buttonImportLater= rootView.findViewById(R.id.btnImportLater)
        buttonStop = rootView.findViewById(R.id.btnStop)
        buttonPlay = rootView.findViewById(R.id.btnPlay)

        buttonRecord.isEnabled = false
        buttonImport.isEnabled = false
        buttonStop.isEnabled = false
        buttonPlay.isEnabled = false
        buttonImportLater.isEnabled = false

        mp = MediaPlayer()
        textView = rootView.findViewById(R.id.txtTime)



        fusedLocationClient= LocationServices.getFusedLocationProviderClient(requireContext())
        checkPermissions()



        buttonRecord.setOnClickListener {
            try {
                startTimer()
                val timestamp = System.currentTimeMillis()
                val audioDir = requireContext().getExternalFilesDir(null)
                val audioFile = File(audioDir, "myrec_$timestamp.mp3")
                path = audioFile.absolutePath

                mediaRec = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(path)

                }
                mediaRec.prepare()
                mediaRec.start()

                buttonStop.isEnabled = true
                buttonRecord.isEnabled = false
                buttonPlay.isEnabled = false
                buttonImport.isEnabled = false
                buttonImportLater.isEnabled = false

            } catch (e: Exception) {
            }
        }

        buttonImportPost.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, ImportFragment())?.commit()
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Pending uploads"
        }


        buttonStop.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            mediaRec.stop()
            buttonStop.isEnabled= false
            buttonRecord.isEnabled = true
            buttonImport.isEnabled = true
            buttonPlay.isEnabled = true

            if(isMobileNetwork(requireContext())){
                buttonImportLater.isEnabled = true
            }

        }

        mp.setOnCompletionListener {
            mp.reset()
            buttonPlay.text = "Riproduci"
        }

        buttonPlay.setOnClickListener {
            if(mp.isPlaying){
                mp.pause()
                mp.reset()
                buttonPlay.text="Riproduci"

            }
            else{
                buttonPlay.text="Pausa"
                mp.setDataSource(path)
                mp.prepare()
                mp.start()
            }
        }

        buttonImport.setOnClickListener {
            buttonImport.isEnabled = false
            buttonImportLater.isEnabled = false
            buttonRecord.isEnabled = false

            uploadFlag = 1
            getCurrentLocation()

        }

        buttonImportLater.setOnClickListener {
            buttonImport.isEnabled = false
            buttonImportLater.isEnabled = false
            buttonRecord.isEnabled = false

            uploadFlag = 2
            getCurrentLocation()
            buttonRecord.isEnabled = true

        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mp.isInitialized) {
            mp.release()
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                val seconds = elapsedTime / 1000
                val minutes = seconds / 60

                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                textView.text = formattedTime
                handler.postDelayed(this, 10)
            }
        })
    }

    fun importLater() {
        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songCounter = sharedPreferences.getInt("songCounter", 0)
        val songLoading = SongImport(0,  latitude, longitude,  path , sharedPreferences.getInt("client_id", 0)   )

        val songInserted = Gson().toJson(songLoading)
        editor.putString("Song$songCounter", songInserted)

        editor.putInt("songCounter", songCounter + 1)
        editor.apply()
    }

    fun isMobileNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = connectivityManager?.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            checkLocationPermissions()
        }
    }

    fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
        } else {
            buttonRecord.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermissions()
                } else {
                    Toast.makeText(requireContext(), "Permessi necessari per la registrazione audio", Toast.LENGTH_SHORT).show()
                }
            }

            10 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buttonRecord.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Permessi di localizzazione negati", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentLocation() {
        try{
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token).addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    if(uploadFlag==1){
                        uploadSong()
                    } else if (uploadFlag == 2){
                        importLater()
                    }
                } else {
                    Toast.makeText(requireContext(), "Non è stato possibile ottenere la posizione", Toast.LENGTH_SHORT).show()
                    buttonRecord.isEnabled= true
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error getting location: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Errore nella chiamata: ${exception.message}")
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Location permission is required to access location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadSong(){
        val homeViewModel: HomeViewModel by viewModels()
        val songDao = SongDatabase.getInstance(requireContext()).songDao()
        homeViewModel.uploadAudio(path, longitude, latitude, songDao, requireContext() )
        homeViewModel.statusCode.observe(viewLifecycleOwner) { code ->
            buttonRecord.isEnabled = true
            if(code == 200){
                Toast.makeText(requireContext(), "File caricato correttamente", Toast.LENGTH_SHORT).show()
            } else if(code == 401) {
                Toast.makeText(requireContext(), "Autenticazione scaduta. Effettuare l'accesso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginView::class.java))
                requireActivity().finish()
            } else if(code == 413) {
                Toast.makeText(requireContext(), "Il file è troppo grande. Riprovare", Toast.LENGTH_SHORT).show()
            } else if(code == 415) {
                Toast.makeText(requireContext(), "Il file non è supportato. Riprovare", Toast.LENGTH_SHORT).show()
            } else if(code == 0) {
                Toast.makeText(requireContext(), "Errore di connessione. Riprovare", Toast.LENGTH_SHORT).show()
            }
            buttonImport.isEnabled = false
        }
    }


}