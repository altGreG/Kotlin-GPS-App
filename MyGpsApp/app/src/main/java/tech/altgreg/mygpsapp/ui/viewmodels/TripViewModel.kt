package tech.altgreg.mygpsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.altgreg.mygpsapp.db.Marker
import tech.altgreg.mygpsapp.db.Trip
import tech.altgreg.mygpsapp.db.TripDatabase
import tech.altgreg.mygpsapp.db.TripRepository

class TripViewModel(application: Application): AndroidViewModel(application) {

    private var readAllData: LiveData<List<Trip>>

    private var readAllMarkers: LiveData<List<Marker>>
    private var repository: TripRepository




    init {
        val tripDAO = TripDatabase.getDatabase(application).getTripDao()
        val markerDAO = TripDatabase.getDatabase(application).getMarkerDao()

        repository = TripRepository(tripDAO, markerDAO)

        readAllData = repository.readAllDataByDate

        readAllMarkers = repository.readAllMarkers

    }

    val tripsSortedByDate = repository.getAllDataByDate()
//    val tripsSortedById = repository.getAllDataById()
    val markers = repository.getAllMarkers()

    fun addTrip(trip: Trip){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTrip(trip)
        }
    }

    fun addMarker(marker: Marker){
        viewModelScope.launch(Dispatchers.IO){
            repository.addMarker(marker)
        }
    }


}