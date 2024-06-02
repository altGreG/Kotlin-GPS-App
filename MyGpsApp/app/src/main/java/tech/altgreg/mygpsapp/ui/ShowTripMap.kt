package tech.altgreg.mygpsapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.db.Marker
import tech.altgreg.mygpsapp.db.Trip
import tech.altgreg.mygpsapp.other.Constants
import tech.altgreg.mygpsapp.other.Constants.ACTION_PAUSE_SERVICE
import tech.altgreg.mygpsapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import tech.altgreg.mygpsapp.other.Constants.ACTION_STOP_SERVICE
import tech.altgreg.mygpsapp.other.Constants.MAP_ZOOM
import tech.altgreg.mygpsapp.other.Constants.POLYLINE_COLOR
import tech.altgreg.mygpsapp.other.Constants.POLYLINE_WIDTH
import tech.altgreg.mygpsapp.other.Constants.REQUEST_PERMISSION_CODE_1
import tech.altgreg.mygpsapp.other.Constants.REQUEST_PERMISSION_CODE_2
import tech.altgreg.mygpsapp.other.TrackingUtility
import tech.altgreg.mygpsapp.services.Polyline
import tech.altgreg.mygpsapp.services.TrackingService
import tech.altgreg.mygpsapp.ui.viewmodels.TripViewModel
import java.util.Calendar

class ShowTripMap : AppCompatActivity() , EasyPermissions.PermissionCallbacks{

    private var map: GoogleMap? = null
    lateinit var mapView: MapView

    private var isTracking = false
//    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeInMillis = 0L

    lateinit var btnToggleRun: Button
    lateinit var btnFinishRun: Button
    lateinit var addLocationBtn: Button
    lateinit var tvTimer: TextView

//    lateinit var rvTrips: RecyclerView

    var isStarted = false

    private val tripViewModel: TripViewModel by viewModels()

    companion object {
        var tripMarkers =  mutableListOf<Marker>()
//        val addedMarker = MutableLiveData<Boolean>()
        var pathPoints = mutableListOf<Polyline>()
    }

//    private lateinit var tripAdapter: TripAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_trip_map)

        requestPermissions()


        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        btnToggleRun = findViewById(R.id.addToMapBtn)
        btnFinishRun = findViewById(R.id.btnFinishRun)
        addLocationBtn = findViewById(R.id.addLocationBtn)
        tvTimer = findViewById(R.id.tvTimer)

        btnToggleRun.setOnClickListener{
            toggleRun()
        }

        btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()
            endTripAndSaveToDb()
        }

        addLocationBtn.setOnClickListener {
            goToMarkerDetails()
        }

        subscribeToObservers()
    }

//    private fun setupRecyclerView() = rvTrips.apply {
//        tripAdapter = TripAdapter()
//        adapter = tripAdapter
//        layoutManager = LinearLayoutManager(context)
//    }




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
//            Log.d("Timer", "${formattedTime}")
        })

    }

    fun goToMarkerDetails(){
        Intent(this, EditMarkerDetails::class.java).also {
            startActivity(it)
            finish()
        }
    }

//    fun putMarkerOnLastLocation(){
//
//
//            Log.d("Markers", "${tripMarkers.last().posLat} ::: ${tripMarkers.last().posLgd}")
//
//            map?.addMarker(
//                MarkerOptions()
//                .position(LatLng(tripMarkers.last().posLat, tripMarkers.last().posLgd))
//                .title(tripMarkers.last().localization))
//
//
//    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        if(intent?.action == Constants.ACTION_PUT_MARKER){
//            Toast.makeText(this,"Successfully added marker", Toast.LENGTH_SHORT).show()
//        }
        addAllPolylines()

    }

    fun putMarkerIfNeeded(intent: Intent?){
        if(intent?.action == Constants.ACTION_PUT_MARKER){
//            putMarkerOnLastLocation()
            Toast.makeText(this,"Successfully added marker", Toast.LENGTH_SHORT).show()
        }
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
        if(!isStarted){
            isStarted = true
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.GONE
        }else {
            if(!isTracking) {
                btnToggleRun.text = "Resume"
                btnFinishRun.visibility = View.VISIBLE
                addLocationBtn.visibility = View.VISIBLE
            } else {
                btnToggleRun.text = "Stop"
                btnFinishRun.visibility = View.GONE
                addLocationBtn.visibility = View.GONE
            }
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

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endTripAndSaveToDb(){

        map?.snapshot {bmp ->
            var distanceInMeter = 0
            for (polyline in pathPoints){
                distanceInMeter += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val trip = Trip(bmp, dateTimestamp, distanceInMeter,curTimeInMillis)
            tripViewModel.addTrip(trip)
            for (marker in tripMarkers){
                tripViewModel.addMarker(marker)
            }
            Toast.makeText(this,"Trip saved successfully", Toast.LENGTH_LONG).show()
            stopTrip()
            tripMarkers =  mutableListOf<Marker>()

        }
    }

    private fun stopTrip() {
        sendCommandToService(ACTION_STOP_SERVICE)
        Intent(this, MainActivity::class.java).also {   // forchange
            startActivity(it)
            finish()
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

        for (marker in tripMarkers) {
            map?.addMarker(
                MarkerOptions()
                    .position(LatLng(marker.posLat, marker.posLgd))
                    .title(marker.localization))
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
                REQUEST_PERMISSION_CODE_1,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept these permissions to use this app.",
                REQUEST_PERMISSION_CODE_1,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,

                )
            EasyPermissions.requestPermissions(
                this,
                "You need to accept these permissions to use this app.",
                REQUEST_PERMISSION_CODE_2,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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