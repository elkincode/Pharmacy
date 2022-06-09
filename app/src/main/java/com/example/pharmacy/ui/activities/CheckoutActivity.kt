package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Cart
import com.example.pharmacy.models.Drug
import com.example.pharmacy.models.Order
import com.example.pharmacy.ui.adapters.CartListAdapter

class CheckoutActivity : AppCompatActivity() {

    private lateinit var mDrugList: ArrayList<Drug>
    private lateinit var mCartItemsList: ArrayList<Cart>

    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        val btnConfirm = findViewById<Button>(R.id.btn_place_order)

        btnConfirm.setOnClickListener {

            val address = findViewById<android.widget.EditText>(R.id.et_address)

            if (TextUtils.isEmpty(address.text.toString().trim { it <= ' ' })) {
                Toast.makeText(
                    this@CheckoutActivity,
                    "Заполните адрес",
                    Toast.LENGTH_SHORT)
                    .show()
            } else {
                placeAnOrder(address.text.toString())
            }
        }

        getProductList()
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_checkout_activity)
        setSupportActionBar(findViewById(R.id.toolbar_checkout_activity))
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Drug>) {
        mDrugList = productsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList: ArrayList<Cart>) {

        mCartItemsList = cartList

        for (drug in mDrugList) {
            for (cart in cartList) {
                if (drug.product_id == cart.product_id) {
                    cart.stock_quantity = drug.stock_quantity
                }
            }
        }

        val rvCart_list = findViewById<RecyclerView>(R.id.rv_cart_list_items)
        val layout = findViewById<LinearLayout>(R.id.ll_checkout_place_order)
        val sub_total = findViewById<TextView>(R.id.tv_checkout_sub_total)
        val shipping_charge = findViewById<TextView>(R.id.tv_checkout_shipping_charge)
        val total_amount = findViewById<TextView>(R.id.tv_checkout_total_amount)

        rvCart_list.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rvCart_list.setHasFixedSize(true)

        val cartListAdapter = CartListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rvCart_list.adapter = cartListAdapter

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        sub_total.text = "$mSubTotal ₽"
        shipping_charge.text = "300 ₽"

        if (mSubTotal > 0) {
            layout.visibility = View.VISIBLE

            val total = mSubTotal + 300
            total_amount.text = "$total ₽"
        } else {
            layout.visibility = View.GONE
        }
    }

    private fun placeAnOrder(address: String) {

        val order = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            address,
            "Заказ ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "0.0",
            mTotalAmount.toString(),
        )
        FirestoreClass().placeOrder(this@CheckoutActivity, order)
    }


    fun orderPlacedSuccess() {


        Toast.makeText(
            this@CheckoutActivity,
            "Your order placed successfully.",
            Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}