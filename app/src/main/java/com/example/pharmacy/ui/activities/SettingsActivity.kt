package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.pharmacy.Common
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.User
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnExit = findViewById<Button>(R.id.btn_logout)
        btnExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        val btnAdd = findViewById<Button>(R.id.btn_new_drug)
        btnAdd.setOnClickListener {
            FirestoreClass().getUserDetails(this@SettingsActivity)
        }
    }

    fun userLoggedInSuccess(user: User) {
        val intent = Intent(this@SettingsActivity, AddDrugActivity::class.java)
        intent.putExtra("role", user.role)
        startActivity(intent)
        finish()
    }
}