package com.example.firedoc

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

lateinit var bCadastrar : Button
lateinit var tvEmail : TextView
lateinit var bLogin : Button
lateinit var tvContraseña : TextView
lateinit var bEsqueceu : Button
lateinit var username: EditText
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()


        bCadastrar = findViewById(R.id.bCadastrar)
        tvEmail = findViewById(R.id.tvEmail)
        bLogin = findViewById(R.id.bLogin)
        tvContraseña = findViewById(R.id.tvContraseña)
        bEsqueceu = findViewById(R.id.bEsqueceu)


        bCadastrar.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
            finish()
        }

        bLogin.setOnClickListener {
            doLogin()
        }

        bEsqueceu.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Esqueceu sua senha")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val etEmail = view.findViewById<EditText>(R.id.etEmail)
            builder.setView(view)
            builder.setPositiveButton("Renerar", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(etEmail)
            })
            builder.setNegativeButton("Fechar", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }

    }

    private fun forgotPassword(etEmail : EditText){
        if (etEmail.text.toString().isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            return
        }

        auth.sendPasswordResetEmail(etEmail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email enviado.",Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun doLogin() {
        if (tvEmail.text.toString().isEmpty()) {
            tvEmail.error = "Insira email"
            tvEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tvEmail.text.toString()).matches()) {
            tvEmail.error = "Insira um email vàlido"
            tvEmail.requestFocus()
            return
        }

        if (tvContraseña.text.toString().isEmpty()) {
            tvContraseña.error = "Insira sua senha"
            tvContraseña.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(tvEmail.text.toString(), tvContraseña.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {

                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            if(currentUser.isEmailVerified) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }else{
                Toast.makeText(
                    baseContext, "Por favor, verifique seu email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                baseContext, "Falha ao logar.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}