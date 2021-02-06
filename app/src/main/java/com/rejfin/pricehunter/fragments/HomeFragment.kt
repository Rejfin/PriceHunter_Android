package com.rejfin.pricehunter.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import com.rejfin.pricehunter.Item
import com.rejfin.pricehunter.Product
import com.rejfin.pricehunter.R
import com.rejfin.pricehunter.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.lang.IllegalStateException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = createOptionsBuilder()
        try{
            FirebaseApp.initializeApp(requireContext(), builder.build())
        }catch (e : IllegalStateException){
            Log.d("PriceLog", e.message.toString())
        }
    }

    private fun createOptionsBuilder(): FirebaseOptions.Builder{
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val set = pref.getString("firebase_config", "")
        val conf = set!!.replace("[", "").replace("]","").split(",")

        return FirebaseOptions.Builder()
            .setApplicationId(conf[2].trim())
            .setApiKey(conf[3].trim())
            .setDatabaseUrl(conf[0].trim())
            .setStorageBucket(conf[1].trim())
            .setProjectId(conf[4].trim())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.navigationIcon = null
        toolbar.menu.getItem(0).isVisible = true

        if (FirebaseApp.getApps(requireContext()).isNotEmpty()) {
            try{
                // set recycle view
                binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
                binding.rvMain.addItemDecoration(DividerItemDecoration(binding.rvMain.context, DividerItemDecoration.VERTICAL))
                val adapter = GroupAdapter<GroupieViewHolder>()
                binding.rvMain.adapter = adapter

                // set firebase database
                val database = FirebaseDatabase.getInstance()
                val ref = database.getReference("last_prices")

                // listen for data change in firebase database
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.failed)
                            .setMessage(R.string.failed_download_message)
                            .setPositiveButton(R.string.ok){ _, _->}
                            .show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val dataList = dataSnapshot.children
                        for (i in dataList){
                            val data = i.getValue(Product::class.java)
                            adapter.add(Item(data!!))
                        }
                    }
                })

                /**
                 * open browser with selected item url
                 * if more than one link exist, show dialog where user can select shop
                 */

                adapter.setOnItemClickListener { item, _ ->
                    val data = item as Item
                    var browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.product.urls[0]))
                    if(item.product.urls.size > 1){
                        // build options array
                        val newArray = mutableListOf<String>()
                        item.product.urls.forEach {
                            newArray.add(it.split(".")[1])
                        }
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(R.string.choose_shop)
                        builder.setItems(newArray.toTypedArray()){_, which ->
                            browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.product.urls[which]))
                            startActivity(browserIntent)
                        }
                        builder.show()
                    }else{
                        startActivity(browserIntent)
                    }
                }

                // refresh data on swipe down
                binding.swipeLayout.setOnRefreshListener {
                    val configRef = database.getReference("control/forceRefresh")
                    configRef.setValue(1)
                    binding.swipeLayout.isRefreshing = false
                }

                val isRefreshing = database.getReference("control/isRefreshing")

                // if script is refreshing data, show loading bar on top
                isRefreshing.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.getValue(Int::class.java) == 1){
                            binding.smoothProgressBar.visibility = View.VISIBLE
                        }else{
                            binding.smoothProgressBar.visibility = View.INVISIBLE
                        }
                    }
                })
            }catch (a : DatabaseException){
                AlertDialog.Builder(requireContext())
                        .setMessage(a.localizedMessage)
                        .setTitle(R.string.error)
                        .setPositiveButton(R.string.ok){ _, _->}
                        .show()
            }
        }else{
            AlertDialog.Builder(requireContext())
                    .setMessage(R.string.init_error)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.ok){ _, _->}
                    .show()
        }
    }

}