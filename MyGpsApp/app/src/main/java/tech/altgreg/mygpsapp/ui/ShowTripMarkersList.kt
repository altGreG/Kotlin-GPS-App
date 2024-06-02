package tech.altgreg.mygpsapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.adapters.MarkerAdapter
import tech.altgreg.mygpsapp.adapters.TripAdapter
import tech.altgreg.mygpsapp.ui.viewmodels.TripViewModel

class ShowTripMarkersList : AppCompatActivity() {

    private val tripViewModel: TripViewModel by viewModels()
    lateinit var rvMarkers: RecyclerView

    private lateinit var markerAdapter: MarkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_trip_markers_list)



        rvMarkers = findViewById(R.id.rvMarkers)
        setupRecyclerView()

        tripViewModel.markers.observe(this, Observer {
            markerAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() = rvMarkers.apply {
        markerAdapter = MarkerAdapter()
        adapter = markerAdapter
        layoutManager = LinearLayoutManager(context)
    }
}