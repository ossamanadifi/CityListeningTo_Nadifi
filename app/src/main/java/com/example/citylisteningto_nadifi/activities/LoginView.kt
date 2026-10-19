package com.example.citylisteningto_nadifi.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.citylisteningto_nadifi.viewmodels.LoginViewModel
import com.example.citylisteningto_nadifi.R

class LoginView : AppCompatActivity() {


    val loginModel: LoginViewModel by viewModels()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextUsername: EditText = findViewById(R.id.textUsernameLogin)
        val editTextPassword: EditText = findViewById(R.id.textPassLogin)
        val buttonRegister: Button = findViewById(R.id.btnLogin)

        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            loginModel.login(username, password, this)
        }

        loginModel.statusCode.observe(this) { code ->
            if(code == 200){
                startActivity(Intent(this, HomeView::class.java))
                finish()
            } else {
                Toast.makeText(this, "Username e  password non riconosciuti. Riprovare", Toast.LENGTH_SHORT).show()
            }
        }

        val myTextView: TextView = findViewById(R.id.textView3)
        myTextView.setOnClickListener {
            startActivity(Intent(this, RegisterView::class.java))
        }

        val dbText: TextView = findViewById(R.id.txtDb)
        dbText.setOnClickListener {
            startActivity(Intent(this, DatabaseView::class.java))
        }
    }
}