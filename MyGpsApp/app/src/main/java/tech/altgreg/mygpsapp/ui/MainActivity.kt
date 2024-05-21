package tech.altgreg.mygpsapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.other.Constants.ACTION_SHOW_TRIP_ACTIVITY

class MainActivity : AppCompatActivity() {

    lateinit var text: TextView
    lateinit var startTripBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        navigateToShowTripMapIfNeeded(intent)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        text = findViewById(R.id.appNameText)

        startTripBtn = findViewById(R.id.startTripBtn)

        startTripBtn.setOnClickListener {
            Intent(this@MainActivity, ShowTripMap::class.java).also {
                startActivity(it)
                finish()
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
                finish()
            }
        }
    }



}