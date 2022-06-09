package com.example.pharmacy.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pharmacy.Common
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.User
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val photo = findViewById<ImageView>(R.id.iv_user_photo)
        val firstName = findViewById<EditText>(R.id.et_first_name)
        val lastName = findViewById<EditText>(R.id.et_last_name)
        val email = findViewById<EditText>(R.id.et_email)
        val pass = findViewById<EditText>(R.id.et_password)
        val phone = findViewById<EditText>(R.id.et_mobile_number)
        val saveBtn = findViewById<Button>(R.id.btn_add_drug)
        val maleRb = findViewById<RadioButton>(R.id.rb_male)
        val femaleRb = findViewById<RadioButton>(R.id.rb_female)
        val otherRb = findViewById<RadioButton>(R.id.rb_other)

        if (intent.hasExtra(Common.USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Common.USER_DETAILS)!!
        }

        if(intent.hasExtra(Common.USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Common.USER_DETAILS)!!
        }

        firstName.isEnabled = false
        firstName.setText(mUserDetails.firstName)

        lastName.isEnabled = false
        lastName.setText(mUserDetails.lastName)

        email.isEnabled = false
        email.setText(mUserDetails.email)

        photo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser(this@ProfileActivity)
            } else {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2
                )
            }
        }

        saveBtn.setOnClickListener {

            if (validateUserProfileDetails(phone)) {

                if (mSelectedImageFileUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(
                        this@ProfileActivity,
                        mSelectedImageFileUri,
                        Common.PROFILE_IMAGE
                    )
                } else {
                    updateUserProfileDetails(maleRb, femaleRb, phone)
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showImageChooser(this@ProfileActivity)

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, 2)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photo = findViewById<ImageView>(R.id.iv_user_photo)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        photo.setImageURI(Uri.parse(mSelectedImageFileUri.toString()))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(phone: EditText): Boolean {
        return when {

            TextUtils.isEmpty(phone.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_mobile_number),
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun updateUserProfileDetails(male: RadioButton, female: RadioButton, phone: EditText) {

        val userHashMap = HashMap<String, Any>()

        val mobileNumber = phone.text.toString().trim { it <= ' ' }

        val sex = if (male.isChecked) {
            Common.MALE
        } else if (female.isChecked){
            Common.FEMALE
        } else {
            Common.OTHER
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Common.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty()) {
            userHashMap[Common.MOBILE] = mobileNumber.toLong()
        }
        userHashMap[Common.SEX] = sex

        userHashMap[Common.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(
            this@ProfileActivity,
            userHashMap
        )
    }

    fun profileUpdateSuccess() {

        Toast.makeText(
            this@ProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@ProfileActivity, DashboardActivity::class.java))
        finish()
    }
    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        val phone = findViewById<EditText>(R.id.et_mobile_number)
        val male = findViewById<RadioButton>(R.id.rb_male)
        val female = findViewById<RadioButton>(R.id.rb_female)
        updateUserProfileDetails(male, female, phone)
    }
/*
    fun openSomeActivityForResult() {
        val intent = Intent(this, ProfileActivity::class.java)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!
                        val photo = findViewById<ImageView>(R.id.iv_user_photo)

                        photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

 */
}