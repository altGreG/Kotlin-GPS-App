package tech.altgreg.mygpsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.altgreg.mygpsapp.db.Trip
import tech.altgreg.mygpsapp.db.TripDatabase
import tech.altgreg.mygpsapp.db.TripRepository

class TripViewModel(application: Application): AndroidViewModel(application) {

    private var readAllData: LiveData<List<Trip>>
    private var repository: TripRepository



    init {
        val tripDAO = TripDatabase.getDatabase(application).getTripDao()
        repository = TripRepository(tripDAO)
        readAllData = repository.readAllDataByDate
    }

    fun addTrip(trip: Trip){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTrip(trip)
        }
    }

}