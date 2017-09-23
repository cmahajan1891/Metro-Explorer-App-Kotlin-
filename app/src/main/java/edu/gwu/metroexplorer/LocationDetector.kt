package edu.gwu.metroexplorer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

/**
 * Created by cmahajan on 9/23/17.
 */

private const val UPDATE_INTERVAL: Long = 10 * 1000
private const val FASTEST_INTERVAL: Long = 2000

class LocationDetector {

    private lateinit var mLocationRequest: LocationRequest

    fun startLocationUpdates(activity: MenuActivity) {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(activity)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    //super.onLocationResult(p0)
                    onLocationChanged(p0!!.lastLocation, activity)
                }
            },
                    Looper.myLooper())

        }



    }

    fun onLocationChanged(location: Location, activity: MenuActivity) {
        // New location has now been determined
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        Toast.makeText(activity.baseContext, msg, Toast.LENGTH_SHORT).show()
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
    }

}