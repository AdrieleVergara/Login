package com.example.firedoc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth

lateinit var tvEmail2 : TextView
lateinit var tvContrasena : TextView
lateinit var bRegistrar : Button

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        auth = FirebaseAuth.getInstance()

        tvEmail2 = findViewById(R.id.tvEmail2)
        tvContrasena = findViewById(R.id.tvContrasena)
        bRegistrar = findViewById(R.id.bRegistrar)

        bRegistrar.setOnClickListener {
            signUpUser()
        }

    }

    private fun signUpUser() {
        if (tvEmail2.text.toString().isEmpty()) {
            tvEmail2.error = "Por favor, insira um email"
            tvEmail2.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tvEmail2.text.toString()).matches()) {
            tvEmail2.error = "Insira um email valido"
            tvEmail2.requestFocus()
            return
        }

        if (tvContrasena.text.toString().isEmpty()) {
            tvContrasena.error = "Insira sua senha"
            tvContrasena.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tvEmail2.text.toString(),tvContrasena.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext, "Falha ao autenticar, tente mais tarde.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}