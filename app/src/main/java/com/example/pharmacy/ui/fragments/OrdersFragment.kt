package com.example.pharmacy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Order
import com.example.pharmacy.ui.adapters.OrdersListAdapter

class OrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_orders, container, false)
        return root
    }

    fun showOrders(ordersList: ArrayList<Order>) {

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.rv_my_order_items)
        val textView : TextView = requireView().findViewById(R.id.tv_no_orders_found)
        if (ordersList.size > 0) {
            recyclerView.visibility = View.VISIBLE
            textView.visibility = View.GONE

            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)

            val myOrdersAdapter = OrdersListAdapter(requireActivity(), ordersList)
            recyclerView.adapter = myOrdersAdapter
        } else {
            recyclerView.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }
    }

    private fun getMyOrdersList() {
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }
}