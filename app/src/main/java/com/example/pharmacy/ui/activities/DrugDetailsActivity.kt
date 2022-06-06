package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.pharmacy.Constants
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Cart
import com.example.pharmacy.models.Drug

class DrugDetailsActivity : AppCompatActivity() {

    private var mDrugId: String = ""
    private lateinit var mDrugDetails: Drug

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_details)

        val addBtn = findViewById<Button>(R.id.btn_add_to_cart)
        val cartBtn = findViewById<Button>(R.id.btn_go_to_cart)

        if (intent.hasExtra(Constants.EXTRA_DRUG_ID)) {
            mDrugId =
                intent.getStringExtra(Constants.EXTRA_DRUG_ID)!!
            Log.i("Drug Id", mDrugId)
        }

        var drugOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_DRUG_OWNER_ID)) {
            drugOwnerId =
                intent.getStringExtra(Constants.EXTRA_DRUG_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == drugOwnerId) {
            addBtn.visibility = View.GONE
            cartBtn.visibility = View.GONE
        } else {
            addBtn.visibility = View.VISIBLE
        }

        getProductDetails(mDrugId)

        addBtn.setOnClickListener {
            addToCart(mDrugId, mDrugDetails)
        }

        cartBtn.setOnClickListener {
            startActivity(Intent(this@DrugDetailsActivity, CartListActivity::class.java))
        }
    }

    fun productDetailsSuccess(drug: Drug) {

        mDrugDetails = drug

        val title = findViewById<TextView>(R.id.tv_product_details_title)
        val price = findViewById<TextView>(R.id.tv_product_details_price)
        val description = findViewById<TextView>(R.id.tv_product_details_description)
        val quantity = findViewById<TextView>(R.id.tv_product_details_quantity)
        val imageView = findViewById<ImageView>(R.id.iv_product_detail_image)

        Glide
            .with(this@DrugDetailsActivity)
            .load(drug.image) // Uri or URL of the image
            .centerCrop() // Scale type of the image.
            .into(imageView)

        title.text = drug.title
        price.text = "$${drug.price}"
        description.text = drug.description
        quantity.text = drug.stock_quantity

        if (FirestoreClass().getCurrentUserID() == drug.user_id) {
            Log.e("Tes:", drug.user_id)
        } else {
            FirestoreClass().checkIfItemExistInCart(this@DrugDetailsActivity, mDrugId)
        }
    }

    private fun getProductDetails(mDrugId: String) {
        FirestoreClass().getProductDetails(this@DrugDetailsActivity, mDrugId)
    }

    private fun addToCart(drugId: String, drugDetails: Drug) {

        val addToCart = Cart(
            FirestoreClass().getCurrentUserID(),
            drugId,
            drugDetails.title,
            drugDetails.price,
            drugDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        FirestoreClass().addCartItems(this@DrugDetailsActivity, addToCart)
    }

    fun addToCartSuccess() {

        val addBtn = findViewById<Button>(R.id.btn_add_to_cart)
        val cartBtn = findViewById<Button>(R.id.btn_go_to_cart)

        Toast.makeText(
            this@DrugDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        addBtn.visibility = View.GONE
        cartBtn.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        val addBtn = findViewById<Button>(R.id.btn_add_to_cart)
        val cartBtn = findViewById<Button>(R.id.btn_go_to_cart)

        addBtn.visibility = View.GONE
        cartBtn.visibility = View.VISIBLE
    }
}