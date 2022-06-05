package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firstName = findViewById<EditText>(R.id.et_first_name)
        val lastName = findViewById<EditText>(R.id.et_last_name)
        val email = findViewById<EditText>(R.id.et_email)
        val pass = findViewById<EditText>(R.id.et_password)

        val buttonLogin = findViewById<Button>(R.id.loginBtn)
        buttonLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
            registerUser(firstName, lastName, email, pass)
        }
    }

    private fun registerUser(firstName: EditText, lastName: EditText, email: EditText, pass: EditText) {
            val firstNameText: String = firstName.text.toString().trim { it <= ' ' }
            val lastNameText: String = lastName.text.toString().trim { it <= ' ' }
            val emailText: String = email.text.toString().trim { it <= ' ' }
            val passText: String = pass.text.toString().trim { it <= ' ' }


            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                firstNameText,
                                lastNameText,
                                emailText
                            )

                            FirestoreClass().registerUser(this@RegisterActivity, user)

                            Toast.makeText(
                                this@RegisterActivity,
                                "You are registered successfully.  Your user id is ${firebaseUser.uid}",
                                Toast.LENGTH_SHORT
                            ).show()

//                            FirebaseAuth.getInstance().signOut()
//                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//                            finish()

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

    }
    fun userRegistrationSuccess() {
        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
        finish()
    }
}