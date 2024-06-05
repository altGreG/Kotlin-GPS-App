package tech.altgreg.mygpsapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.adapters.TripAdapter
import tech.altgreg.mygpsapp.ui.viewmodels.TripViewModel

class ShowTripsList : AppCompatActivity() {

//    private lateinit var tripViewModel: ViewModel
    private val tripViewModel: TripViewModel by viewModels()
    lateinit var rvTrips: RecyclerView

    private lateinit var tripAdapter: TripAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_trips_list)

        rvTrips = findViewById(R.id.rvTrips)
        setupRecyclerView()

        tripViewModel.tripsSortedByDate.observe(this, Observer {
            tripAdapter.submitList(it)
        })

    }

    private fun setupRecyclerView() = rvTrips.apply {
        tripAdapter = TripAdapter()
        adapter = tripAdapter
        layoutManager = LinearLayoutManager(context)
    }

}