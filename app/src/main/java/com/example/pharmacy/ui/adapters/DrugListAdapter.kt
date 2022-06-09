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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacy.Common
import com.example.pharmacy.R
import com.example.pharmacy.models.Drug
import com.example.pharmacy.ui.activities.DrugDetailsActivity
import com.example.pharmacy.ui.fragments.ProductsFragment


class DrugListAdapter (
    private val context: Context,
    private var list: ArrayList<Drug>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .into(imageView)

            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "${model.price} ₽"


            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DrugDetailsActivity::class.java)
                intent.putExtra(Common.EXTRA_DRUG_ID, model.product_id)
                intent.putExtra(Common.EXTRA_DRUG_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}