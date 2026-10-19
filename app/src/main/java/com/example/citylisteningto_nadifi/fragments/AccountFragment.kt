package com.example.citylisteningto_nadifi.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.citylisteningto_nadifi.activities.LoginView
import com.example.citylisteningto_nadifi.viewmodels.LoginViewModel
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.db.SongDatabase


class AccountFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE )
        val editor = sharedPreferences.edit()

        val txtUsername: TextView = rootView.findViewById(R.id.txtAccountText)
        val txtId: TextView = rootView.findViewById(R.id.txtIdText)
        val buttonDelete: Button = rootView.findViewById(R.id.btnDeleteAccount)

        val loginViewModel : LoginViewModel by viewModels()

        txtUsername.text = sharedPreferences.getString("username", null)
        txtId.text = sharedPreferences.getInt("client_id", 0).toString()


        buttonDelete.setOnClickListener{
            val songDao = SongDatabase.getInstance(requireContext()).songDao()
            val ClientId = sharedPreferences.getInt("client_id" , 0)
            if (ClientId!= 0){
                loginViewModel.deleteAccount(ClientId, songDao)
                startActivity(Intent(requireContext(), LoginView::class.java))
                editor.remove("client_id")
                editor.remove("username")
                editor.apply()

            }
        }




        return rootView
    }


}