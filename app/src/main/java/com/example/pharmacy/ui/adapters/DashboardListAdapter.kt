package com.example.pharmacy.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacy.R
import com.example.pharmacy.models.Drug

open class DashboardListAdapter(
    private val context: Context,
    private var list: ArrayList<Drug>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_dashboard_item_image)

            Glide
                .with(context)
                .load(model.image) // Uri or URL of the image
                .centerCrop() // Scale type of the image.
                .into(imageView)

            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).text = "$${model.price}"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}