package com.example.pharmacy.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.pharmacy.Common
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

        if (intent.hasExtra(Common.EXTRA_DRUG_ID)) {
            mDrugId =
                intent.getStringExtra(Common.EXTRA_DRUG_ID)!!
            Log.i("Drug Id", mDrugId)
        }

        var drugOwnerId: String = ""

        if (intent.hasExtra(Common.EXTRA_DRUG_OWNER_ID)) {
            drugOwnerId =
                intent.getStringExtra(Common.EXTRA_DRUG_OWNER_ID)!!
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

    @SuppressLint("CutPasteId", "SetTextI18n")
    fun productDetailsSuccess(drug: Drug) {

        mDrugDetails = drug

        val title = findViewById<TextView>(R.id.tv_product_details_title)
        val price = findViewById<TextView>(R.id.tv_product_details_price)
        val description = findViewById<TextView>(R.id.tv_product_details_description)
        val quantity = findViewById<TextView>(R.id.tv_product_details_stock_quantity)
        val imageView = findViewById<ImageView>(R.id.iv_product_detail_image)
        val addBtn = findViewById<Button>(R.id.btn_add_to_cart)
        val tv_product_details_stock_quantity = findViewById<TextView>(R.id.tv_product_details_stock_quantity)

        Glide
            .with(this@DrugDetailsActivity)
            .load(drug.image) // Uri or URL of the image
            .centerCrop() // Scale type of the image.
            .into(imageView)

        title.text = drug.title
        price.text = "${drug.price} ₽"
        description.text = drug.description
        quantity.text = drug.stock_quantity

        if (FirestoreClass().getCurrentUserID() == drug.user_id) {
            Log.e("Tes:", drug.user_id)
        } else {
            FirestoreClass().checkIfItemExistInCart(this@DrugDetailsActivity, mDrugId)
        }

        if(drug.stock_quantity.toInt() == 0){
            // Скрыть кнопку, если уже в корзине
            addBtn.visibility = View.GONE
            tv_product_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)
            tv_product_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@DrugDetailsActivity,
                    R.color.red
                )
            )
        }else{

            if (FirestoreClass().getCurrentUserID() == drug.user_id) {
                Log.e("Drug:", drug.user_id)
            } else {
                FirestoreClass().checkIfItemExistInCart(this@DrugDetailsActivity, mDrugId)
            }
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
            Common.DEFAULT_CART_QUANTITY
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