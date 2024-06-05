package tech.altgreg.mygpsapp.ui

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.db.Marker
import tech.altgreg.mygpsapp.other.Constants
import tech.altgreg.mygpsapp.services.TrackingService
import java.lang.Exception

class EditMarkerDetails : AppCompatActivity() {

    lateinit var localizationText: TextView
    lateinit var typeOfTripText: TextView
    lateinit var descriptionText: TextView
    lateinit var imageView: ImageView
    lateinit var pickPhotoBtn: Button
    lateinit var addToMapBtn: Button

    lateinit var localization: String
    lateinit var typeOfTrip: String
    lateinit var description: String
    lateinit var imageUri: Uri

    lateinit var imageBitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_marker_details)

        localizationText = findViewById(R.id.localization)
        typeOfTripText = findViewById(R.id.tripType)
        descriptionText = findViewById(R.id.description)
        imageView = findViewById(R.id.image)
        pickPhotoBtn = findViewById(R.id.pickPhotoBtn)
        addToMapBtn = findViewById(R.id.addToMapBtn)

        pickPhotoBtn.setOnClickListener {
            getPhotoFromGallery()
        }

        addToMapBtn.setOnClickListener{

            try {
                val marker = getAllDataFromForm()
                ShowTripMap.tripMarkers.add(marker)
                Intent(this, ShowTripMap::class.java).also {
                    it.action = Constants.ACTION_PUT_MARKER
                    startActivity(it)
                    finish()
                }
            }catch (e:Exception){
                Toast.makeText(this,"Fill all inputs :)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllDataFromForm():Marker{
        localization = localizationText.text.toString()
        typeOfTrip = typeOfTripText.text.toString()
        description = descriptionText.text.toString()

        val marker: Marker = Marker(localization,typeOfTrip,description, imageBitmap)
        marker.posLat = ShowTripMap.pathPoints.last().last().latitude
        marker.posLgd = ShowTripMap.pathPoints.last().last().longitude

        return marker
    }

    private fun getPhotoFromGallery(){
        var newIntent: Intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(newIntent, 3)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            if (selectedImage != null) {
                imageUri = selectedImage
                imageView.setImageURI(imageUri)
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }

        }
    }
}