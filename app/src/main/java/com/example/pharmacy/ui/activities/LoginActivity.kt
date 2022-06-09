package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pharmacy.Common
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.et_email)
        val pass = findViewById<EditText>(R.id.et_password)

        val buttonLogin = findViewById<Button>(R.id.btn_login)
        val buttonRegister = findViewById<Button>(R.id.registerBtn)

        buttonLogin.setOnClickListener {
            userLogin(email, pass)
        }

        buttonRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
    }

    private fun userLogin(mail: EditText, pass: EditText) {
            val email = mail.text.toString().trim { it <= ' ' }
            val password = pass.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)

                        Toast.makeText(
                            this@LoginActivity,
                            "Вы вошли в систему.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    fun userLoggedInSuccess(user: User) {

        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
            intent.putExtra(Common.USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }
}