package com.example.pharmacy.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.example.pharmacy.models.Drug
import java.io.IOException

class AddDrugActivity : AppCompatActivity() {


    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    var userRole = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drug)

        if (intent.hasExtra("role")) {
            userRole = intent.getIntExtra("role", 0)
            Log.e("userRole", userRole.toString())
        }

        val addBtn = findViewById<Button>(R.id.btn_add_drug)
        val addPicBtn = findViewById<ImageView>(R.id.iv_add_update_product)
        val title = findViewById<EditText>(R.id.et_product_title)
        val price = findViewById<EditText>(R.id.et_product_price)
        val description = findViewById<EditText>(R.id.et_product_description)
        val quantity = findViewById<EditText>(R.id.et_product_quantity)
        val image = findViewById<ImageView>(R.id.iv_product_image)
        val tvNotAdmin = findViewById<TextView>(R.id.you_not_admin)

        if (userRole == 5) {
            addPicBtn.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    showImageChooser(this@AddDrugActivity)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        2
                    )
                }
            }

            addBtn.setOnClickListener {
                if (validateProductDetails(title, price, description, quantity)) {
                    uploadProductImage()
                }
            }
        } else {
            addPicBtn.visibility = android.view.View.GONE
            addBtn.visibility = android.view.View.GONE
            title.visibility = android.view.View.GONE
            price.visibility = android.view.View.GONE
            description.visibility = android.view.View.GONE
            quantity.visibility = android.view.View.GONE
            image.visibility = android.view.View.GONE

            tvNotAdmin.visibility = android.view.View.VISIBLE

            Toast.makeText(
                this@AddDrugActivity,
                "Только администратор может добавлять продукты.",
                Toast.LENGTH_LONG
            ).show()
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
                showImageChooser(this@AddDrugActivity)
            } else {

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
        val photo = findViewById<ImageView>(R.id.iv_product_image)
        val icon = findViewById<ImageView>(R.id.iv_add_update_product)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                if (data != null) {

                    icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddDrugActivity,
                            R.drawable.ic_edit
                        )
                    )

                    try {
                        mSelectedImageFileUri = data.data!!
                        photo.setImageURI(Uri.parse(mSelectedImageFileUri.toString()))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddDrugActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {

            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateProductDetails(title: EditText, price: EditText,
                                       description: EditText, quantity: EditText): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                Toast.makeText(
                    this@AddDrugActivity,
                    resources.getString(R.string.err_msg_select_product_image),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(title.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddDrugActivity,
                    resources.getString(R.string.err_msg_enter_product_title),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(price.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddDrugActivity,
                    resources.getString(R.string.err_msg_enter_product_price),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(description.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddDrugActivity,
                    resources.getString(R.string.err_msg_enter_product_description),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            TextUtils.isEmpty(quantity.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this@AddDrugActivity,
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage() {

        FirestoreClass().uploadImageToCloudStorage(
            this@AddDrugActivity,
            mSelectedImageFileUri,
            Common.DRUG_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {

        mProductImageURL = imageURL

        uploadProductDetails()
    }

    private fun uploadProductDetails() {

        val title = findViewById<EditText>(R.id.et_product_title)
        val price = findViewById<EditText>(R.id.et_product_price)
        val description = findViewById<EditText>(R.id.et_product_description)
        val quantity = findViewById<EditText>(R.id.et_product_quantity)

        val username =
            this.getSharedPreferences(Common.PREF, Context.MODE_PRIVATE)
                .getString(Common.LOG_USER, "")!!

        val drug = Drug(
            FirestoreClass().getCurrentUserID(),
            username,
            title.text.toString().trim { it <= ' ' },
            price.text.toString().trim { it <= ' ' },
            description.text.toString().trim { it <= ' ' },
            quantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddDrugActivity, drug)
    }

    fun productUploadSuccess() {

        Toast.makeText(
            this@AddDrugActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}