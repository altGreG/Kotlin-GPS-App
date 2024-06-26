package tech.altgreg.mygpsapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.other.Constants.ACTION_SHOW_TRIP_ACTIVITY

class MainActivity : AppCompatActivity() {

    lateinit var text: TextView
    lateinit var startTripBtn: Button
    lateinit var showTripsBtn: Button
    lateinit var showMarkersBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigateToShowTripMapIfNeeded(intent)

        text = findViewById(R.id.appNameText)

        startTripBtn = findViewById(R.id.startTripBtn)
        showTripsBtn = findViewById(R.id.showTripsBtn)
        showMarkersBtn = findViewById(R.id.showMarkersBtn)

        startTripBtn.setOnClickListener {
            Intent(this@MainActivity, ShowTripMap::class.java).also {
                startActivity(it)
//                finish()
            }

        }

        showTripsBtn.setOnClickListener {
            Intent(this@MainActivity, ShowTripsList::class.java).also {
                    startActivity(it)
            }

        }

        showMarkersBtn.setOnClickListener {
            Intent(this@MainActivity, ShowTripMarkersList::class.java).also {
                startActivity(it)
            }

        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToShowTripMapIfNeeded(intent)
    }



    private fun navigateToShowTripMapIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRIP_ACTIVITY){
            Intent(this@MainActivity, ShowTripMap::class.java).also {
                startActivity(it)
//                finish()
            }
        }
    }



}