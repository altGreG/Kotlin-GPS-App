package tech.altgreg.mygpsapp.other

import android.graphics.Color

object Constants {
    const val TRIP_DATABASE_NAME = "trip_db"

    const val REQUEST_PERMISSION_CODE_1 = 1
    const val REQUEST_PERMISSION_CODE_2 = 2
    const val REQUEST_PERMISSION_CODE_3 = 3
    const val REQUEST_PERMISSION_CODE_4 = 4
    const val REQUEST_PERMISSION_CODE_5 = 5

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRIP_ACTIVITY = "SHOW_TRACKING_ACTIVITY"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val TIMER_UPDATE_INTERVAL = 50L

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val REQUEST_CODE_GET_IMAGE = 101

    const val ACTION_PUT_MARKER = "ACTION_PUT_MARKER"
}