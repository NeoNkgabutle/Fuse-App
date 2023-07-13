package com.example.thefuseapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.thefuseapp.databinding.ActivityRegistrationScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationScreen : AppCompatActivity() {
    private lateinit var binding:ActivityRegistrationScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Registering Profile")
        progressBar.setCanceledOnTouchOutside(false)
        binding.registeredBtn.setOnClickListener {
            ValidateUserData()
        }


    }
    private var name =""
    private var email =""
    private var username =""
    private var password =""
    private fun ValidateUserData() {
        name = binding.name.text.toString().trim()
        email = binding.email.text.toString().trim()
        username = binding.username.text.toString().trim()
        password= binding.passwordRegister.text.toString().trim()

        if(name.isEmpty() && email.isEmpty() && username.isEmpty() && password.isEmpty()){
            Toast.makeText(this,"Please fill in all the forms",Toast.LENGTH_SHORT).show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"The email doesn't exist",Toast.LENGTH_SHORT).show()
        }else{
            RegisterUser()
        }
    }

    private fun RegisterUser() {
        progressBar.setMessage("Creating User Account")
        progressBar.show()

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            userData()
        }.addOnFailureListener {
            progressBar.dismiss()
            Toast.makeText(this,"User not registered process failed",Toast.LENGTH_SHORT).show()

        }
    }

    private fun userData() {
        progressBar.setMessage("Registering User..")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid
        val hashMap : HashMap<String,Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["timestamp"] = timestamp

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(uid!!).setValue(hashMap).addOnSuccessListener {
            progressBar.dismiss()
            Toast.makeText(this,"Account created",Toast.LENGTH_SHORT).show()
            val Login = Intent(this, loginScreen::class.java)
            startActivity(Login)
        }.addOnFailureListener {
            progressBar.dismiss()
            Toast.makeText(this,"Did not save due to ${it.message}",Toast.LENGTH_SHORT).show()
        }
    }
}
