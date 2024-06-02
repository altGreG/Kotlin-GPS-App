package tech.altgreg.mygpsapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker_table")
data class Marker(
    var localization: String = "",
    var typeOfTrip: String = "",
    var description: String = "",
    var img: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var posLgd: Double = 0.0
    var posLat: Double = 0.0
}