package tech.altgreg.mygpsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trip_table ORDER BY timestamp DESC")
    fun getAllTripsSortedByDate(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: Marker)

    @Query("SELECT * FROM trip_table ORDER BY id DESC")
    fun getAllTripsSortedById(): LiveData<List<Trip>>



}