package com.example.pharmacy.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacy.Common
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Cart
import com.example.pharmacy.ui.activities.CartListActivity
import com.example.pharmacy.ui.activities.DrugDetailsActivity


class CartListAdapter(
    private val context: Context,
    private var list: ArrayList<Cart>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_cart_item_image)

            Glide
                .with(context)
                .load(model.image) // Uri or URL of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.

            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_price).text = "${model.price} ₽"
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DrugDetailsActivity::class.java)
                intent.putExtra(Common.EXTRA_DRUG_ID, model.product_id)
                intent.putExtra(Common.EXTRA_DRUG_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }

            if (model.cart_quantity == "0") {
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE

                if (updateCartItems) {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            } else {

                if (updateCartItems) {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                )
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {
                FirestoreClass().removeItemFromCart(context, model.id)
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {

                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Common.CART_QUANTITY] = (cartQuantity - 1).toString()

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }

            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {

                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Common.CART_QUANTITY] = (cartQuantity + 1).toString()


                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        Toast.makeText(
                            this.context,
                            context.resources.getString(R.string.msg_for_available_stock),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
