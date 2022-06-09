package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
        val conf = findViewById<EditText>(R.id.et_confirm_password)

        val buttonLogin = findViewById<Button>(R.id.loginBtn)
        buttonLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
            registerUser(firstName, lastName, email, pass, conf)
        }
    }

    private fun registerUser(firstName: EditText, lastName: EditText, email: EditText, pass: EditText, conf: EditText) {

            if (validateRegisterDetails(firstName, lastName, email, pass, conf)) {

                val firstNameText: String = firstName.text.toString().trim { it <= ' ' }
                val lastNameText: String = lastName.text.toString().trim { it <= ' ' }
                val emailText: String = email.text.toString().trim { it <= ' ' }
                val passText: String = pass.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->

                            if (task.isSuccessful) {

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
                                    "Успешная регистрация: ${firebaseUser.uid}",
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
    }

    private fun validateRegisterDetails(firstName: EditText, lastName: EditText, email: EditText, pass: EditText, conf: EditText): Boolean {
        return when {
            TextUtils.isEmpty(firstName.text.toString().trim { it <= ' ' }) -> {
                Log.e("firstName: ", firstName.text.toString())
                Toast.makeText(
                    this@RegisterActivity,
                    "Некорректное имя",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(lastName.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Некорректная фамилия",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Некорректный email",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(pass.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@RegisterActivity,
                    "Некорректный пароль",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    fun userRegistrationSuccess() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}