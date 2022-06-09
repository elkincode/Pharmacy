package com.example.pharmacy.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.pharmacy.Common
import com.example.pharmacy.R

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv_main = findViewById<TextView>(R.id.tv_main)

        val sharedPreferences =
            getSharedPreferences(Common.PREF, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Common.LOG_USER, "")!!

        tv_main.text= "The logged in user is $username."

    }
}