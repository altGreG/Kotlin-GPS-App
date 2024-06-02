package tech.altgreg.mygpsapp.db

import androidx.lifecycle.LiveData

class TripRepository(private val tripDao: TripDAO, private val markerDao: MarkerDAO) {

    val readAllDataByDate: LiveData<List<Trip>> = tripDao.getAllTripsSortedByDate()
    val readAllMarkers: LiveData<List<Marker>> = markerDao.getAllMarkers()

    suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun addMarker(marker: Marker) {
        markerDao.insertMarker(marker)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    fun getAllDataByDate() = tripDao.getAllTripsSortedByDate()
    fun getAllMarkers() = markerDao.getAllMarkers()







}