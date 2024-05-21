package tech.altgreg.mygpsapp.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.other.Constants.ACTION_PAUSE_SERVICE
import tech.altgreg.mygpsapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import tech.altgreg.mygpsapp.other.Constants.MAP_ZOOM
import tech.altgreg.mygpsapp.other.Constants.POLYLINE_COLOR
import tech.altgreg.mygpsapp.other.Constants.POLYLINE_WIDTH
import tech.altgreg.mygpsapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import tech.altgreg.mygpsapp.other.TrackingUtility
import tech.altgreg.mygpsapp.services.Polyline
import tech.altgreg.mygpsapp.services.TrackingService

class ShowTripMap : AppCompatActivity() , EasyPermissions.PermissionCallbacks{

    private var map: GoogleMap? = null
    lateinit var mapView: MapView

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeInMillis = 0L

    lateinit var btnToggleRun: Button
    lateinit var btnFinishRun: Button
    lateinit var tvTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_trip_map)
        requestPermissions()

        mapView = findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        btnToggleRun = findViewById(R.id.btnToggleRun)
        btnFinishRun = findViewById(R.id.btnFinishRun)
        tvTimer = findViewById(R.id.tvTimer)

        btnToggleRun.setOnClickListener{
            toggleRun()
        }

        subscribeToObservers()

    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(this, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(this, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(this, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            tvTimer.text = formattedTime
            Log.d("Timer", "${formattedTime}")
        })

    }

    private fun toggleRun(){
        if(isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            btnToggleRun.text = "Resume"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last()[pathPoints.last().size-1]

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String){
        Intent(this@ShowTripMap, TrackingService::class.java).also {
            it.action = action
            startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mapView?.onDestroy()
//    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermissions(this)) {
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }




}