package com.example.pharmacy.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Cart
import com.example.pharmacy.models.Drug
import com.example.pharmacy.ui.adapters.CartListAdapter

class CartListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mDrugList: ArrayList<Drug>
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setupActionBar()

        val btn_checkout = findViewById<Button>(R.id.btn_checkout)

        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, CheckoutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_cart_list_activity)
        setSupportActionBar(findViewById(R.id.toolbar_cart_list_activity))
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun successCartItemsList(cartList: ArrayList<Cart>) {

        for (product in mDrugList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        val rvCart_list = findViewById<RecyclerView>(R.id.rv_cart_items_list)
        val layout = findViewById<LinearLayout>(R.id.ll_checkout)
        val tvNoFound = findViewById<TextView>(R.id.tv_no_cart_item_found)
        val sub_total = findViewById<TextView>(R.id.tv_sub_total)
        val shipping_charge = findViewById<TextView>(R.id.tv_shipping_charge)
        val total_amount = findViewById<TextView>(R.id.tv_total_amount)

        if (mCartListItems.size > 0) {

            rvCart_list.visibility = View.VISIBLE
            layout.visibility = View.VISIBLE
            tvNoFound.visibility = View.GONE

            rvCart_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rvCart_list.setHasFixedSize(true)

            val cartListAdapter = CartListAdapter(this@CartListActivity, mCartListItems, true)
            rvCart_list.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {

                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            sub_total.text = "$subTotal ₽"

            shipping_charge.text = "300 ₽"

            if (subTotal > 0) {
                layout.visibility = View.VISIBLE

                val total = subTotal + 300
                total_amount.text = "$total ₽"
            } else {
                layout.visibility = View.GONE
            }

        } else {
            rvCart_list.visibility = View.GONE
            layout.visibility = View.GONE
            tvNoFound.visibility = View.VISIBLE
        }
    }

    private fun getProductList() {
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Drug>) {
        mDrugList = productsList
        getCartItemsList()

    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    fun itemRemovedSuccess() {

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    fun itemUpdateSuccess() {
        getCartItemsList()
    }

}