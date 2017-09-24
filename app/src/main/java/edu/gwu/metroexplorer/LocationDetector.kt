package edu.gwu.metroexplorer

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/**
 * Created by cmahajan on 9/23/17.
 */

class LocationDetector {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var latLng: LatLng

    private fun getProvider(activity: MapsActivity): FusedLocationProviderClient {

        return LocationServices.getFusedLocationProviderClient(activity)
    }

    fun getLastKnownLocation(activity: MapsActivity, mMap: GoogleMap) {
        mFusedLocationClient = getProvider(activity)
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.lastLocation
                    .addOnSuccessListener(activity, { location ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latLng = LatLng(location.latitude, location.longitude)
                            mMap.addMarker(MarkerOptions().position(latLng).title("Marker at your location."))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        }
                    })
        }

    }
}