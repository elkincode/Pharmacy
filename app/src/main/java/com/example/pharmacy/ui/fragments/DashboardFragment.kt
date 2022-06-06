package com.example.pharmacy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacy.R
import com.example.pharmacy.database.FirestoreClass
import com.example.pharmacy.models.Drug
import com.example.pharmacy.ui.activities.SettingsActivity
import com.example.pharmacy.ui.adapters.DashboardListAdapter

class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Option menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)*/

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }

    private fun getDashboardItemsList() {
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Drug>) {

        recyclerView = requireView().findViewById(R.id.rv_dashboard_items)
        val tv : TextView = requireView().findViewById(R.id.tv_no_dashboard_items_found)

        if (dashboardItemsList.size > 0) {

            recyclerView.visibility = View.VISIBLE
            tv.visibility = View.GONE

            recyclerView.layoutManager = GridLayoutManager(activity, 2)
            recyclerView.setHasFixedSize(true)

            val adapter = DashboardListAdapter(requireActivity(), dashboardItemsList)
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            tv.visibility = View.VISIBLE
        }
    }
}