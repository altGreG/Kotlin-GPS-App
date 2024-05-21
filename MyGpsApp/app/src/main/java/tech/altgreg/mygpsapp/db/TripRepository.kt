package tech.altgreg.mygpsapp.db

import androidx.lifecycle.LiveData

class TripRepository(private val tripDao: TripDAO) {

    val readAllDataByDate: LiveData<List<Trip>> = tripDao.getAllTripsSortedByDate()

    suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    fun getAllDataByDate() = tripDao.getAllTripsSortedByDate()
    fun getAllDataByTime() = tripDao.getAllTripsSortedByTimeInMillis()
    fun getAllDataByDistance() = tripDao.getAllTripsSortedByDistanceInMeters()






}