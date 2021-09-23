package com.example.marvans

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.marvans.databinding.ActivitySignInBinding
import com.example.marvans.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()

            val passwordRegex = Pattern.compile("^" +
                "(?=.*[-@#$%^&+=])" +
                ".{6,}" +
                "$")

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(baseContext, "Ingresar un email valido", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty() || !passwordRegex.matcher(password).matches()) {
                Toast.makeText(baseContext, "La contraseña es debil", Toast.LENGTH_SHORT).show()
            }else if(password != repeatPassword){
                Toast.makeText(baseContext, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
            }else {
                createAccount(email, password)
            }
        }

        binding.backImageView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified){
                reload()
            } else {
                val intent = Intent(this, CheckEmailActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun createAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, CheckEmailActivity::class.java)
                    startActivity(intent)
                }else {
                    Log.w("TAG", "createUserwithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reload(){
        val intent = Intent (this, MainActivity::class.java)
        this.startActivity(intent)
    }
}