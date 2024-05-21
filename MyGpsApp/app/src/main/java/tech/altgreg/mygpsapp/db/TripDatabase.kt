package tech.altgreg.mygpsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.altgreg.mygpsapp.other.Constants.TRIP_DATABASE_NAME


@TypeConverters(Converters::class)
@Database(
    entities = [Trip::class],
    version = 1
)
abstract class TripDatabase : RoomDatabase() {

    abstract fun getTripDao(): TripDAO

    companion object{
        @Volatile
        private var INSTANCE: TripDatabase? = null

        fun getDatabase(context: Context): TripDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripDatabase::class.java,
                    TRIP_DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}