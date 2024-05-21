package tech.altgreg.mygpsapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.ui.viewmodels.TripViewModel

class ShowTripsList : AppCompatActivity() {

    private lateinit var tripViewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_trips_list)

        tripViewModel = ViewModelProvider(this).get(TripViewModel::class.java)


    }
}