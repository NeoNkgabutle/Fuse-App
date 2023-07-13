package com.example.thefuseapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.thefuseapp.databinding.ActivityLoginScreenBinding
import com.example.thefuseapp.databinding.NavHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class loginScreen : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressDialog
    private lateinit var bindingNav: NavHeaderBinding
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressBar = ProgressDialog(this)
        progressBar.setTitle("Logging in")
        progressBar.setCanceledOnTouchOutside(false)

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegistrationScreen::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            validateLoginData()
        }
    }

    private var email = ""
    private var password = ""
    private fun validateLoginData() {
        progressBar.show()
        email = binding.EmailLoginScreen.text.toString().trim()
        password = binding.passwordLoginScreen.text.toString().trim()
        if ( password.isNullOrEmpty()){
            progressBar.dismiss()
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            progressBar.dismiss()
            Toast.makeText(this, "Invalid email format ", Toast.LENGTH_SHORT).show()
        } else if (email.isNullOrEmpty()) {
            progressBar.dismiss()
            Toast.makeText(this, "Please enter in all field", Toast.LENGTH_SHORT).show()
        } else{
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    progressBar.dismiss()
                    val login = Intent(this, MainActivity::class.java)
                    startActivity(login)
                }else{
                    progressBar.dismiss()
                    Toast.makeText(this,"Email or Password is Wrong",Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}


