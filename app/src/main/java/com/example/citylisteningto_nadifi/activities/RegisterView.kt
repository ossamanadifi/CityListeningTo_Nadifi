package com.example.citylisteningto_nadifi.activities

import com.example.citylisteningto_nadifi.viewmodels.RegisterViewModel
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
import com.example.citylisteningto_nadifi.R

class RegisterView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerModel: RegisterViewModel by viewModels()

        val editTextUsername: EditText = findViewById(R.id.textUsernameRegister)
        val editTextPassword: EditText = findViewById(R.id.textPassRegister)
        val buttonRegister: Button = findViewById(R.id.btnRegister)

        val myTextView: TextView = findViewById(R.id.textView4)
        myTextView.setOnClickListener {startActivity(Intent(this, LoginView::class.java))

        }

        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            registerModel.register(username, password)
        }

        registerModel.statusCode.observe(this) { code ->
            if(code==200){
                startActivity(Intent(this, LoginView::class.java))

            } else if(code==400){
                Toast.makeText(this, "Username già presente. Riprovare", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Errore di connesione. Riprovare", Toast.LENGTH_SHORT).show()
            }

        }

        registerModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
