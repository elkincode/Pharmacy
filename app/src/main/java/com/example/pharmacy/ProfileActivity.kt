package com.example.pharmacy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.pharmacy.models.User

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val firstName = findViewById<EditText>(R.id.et_first_name)
        val lastName = findViewById<EditText>(R.id.et_last_name)
        val email = findViewById<EditText>(R.id.et_email)
        val pass = findViewById<EditText>(R.id.et_password)

        // Create a instance of the User model class.
        var userDetails: User = User()
        if(intent.hasExtra(Constants.USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            userDetails = intent.getParcelableExtra(Constants.USER_DETAILS)!!
        }

        firstName.isEnabled = false
        firstName.setText(userDetails.firstName)

        lastName.isEnabled = false
        lastName.setText(userDetails.lastName)

        email.isEnabled = false
        email.setText(userDetails.email)
    }
}