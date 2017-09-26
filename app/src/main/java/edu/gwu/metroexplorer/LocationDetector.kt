package edu.gwu.metroexplorer

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


/**
 * Created by cmahajan on 9/23/17.
 */

const val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
const val KEY_LOCATION = "location"
private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
const val REQUEST_CHECK_SETTINGS = 0x1
const val REQUEST_PERMISSIONS_REQUEST_CODE = 34


class LocationDetector {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mSettingsClient: SettingsClient

    private var mUpdateUIRequired: Boolean = true
    var mCurrentLocation: Location? = null
    var mRequestingLocationUpdates: Boolean = true

    val tag = LocationDetector::class.java.simpleName!!

    fun getLastKnownLocation(activity: MapsActivity) {
        if (checkSelfPermission(activity.baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            mFusedLocationClient.flushLocations()
            mSettingsClient = LocationServices.getSettingsClient(activity)
            mFusedLocationClient.lastLocation
                    .addOnSuccessListener(activity) { location ->

                        updateUI(activity, arrayListOf(location))
                        //TODO can set the mRequestingLocationUpdates to true here
                    }
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    fun updateValuesFromBundle(savedInstanceState: Bundle?, activity: MapsActivity) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES)
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            }

            if (mUpdateUIRequired) {
                updateUI(activity, arrayListOf(mCurrentLocation!!))
            }
        }
    }

    fun updateUI(activity: MapsActivity, locationResult: List<Location>?) {
        // Got last known location. In some rare situations this can be null.
        for (location in locationResult!!) {
            if (location != null) {
                // Logic to handle location object
                mUpdateUIRequired = false
                mCurrentLocation = location
                val lat = location.latitude
                val lng = location.longitude
                Toast.makeText(activity.baseContext, "Longitude: $lng Latitude: $lat", Toast.LENGTH_SHORT).show()
            } else {
                mRequestingLocationUpdates = true
            }
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */

    //TODO implement the Permissions Logic

    fun startLocationUpdates(activity: MapsActivity) {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(activity) { locationResponse ->
                    Log.i(tag, "All location settings are satisfied.")
                    locationResponse.locationSettingsStates
                    if (checkSelfPermission(activity.baseContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                object : LocationCallback() {

                                    override fun onLocationResult(p0: LocationResult?) {
                                        super.onLocationResult(p0)
                                        updateUI(activity, p0?.locations)
                                    }

                                    override fun onLocationAvailability(p0: LocationAvailability?) {
                                        super.onLocationAvailability(p0)
                                        p0?.isLocationAvailable
                                        p0?.describeContents()
                                    }

                                }, Looper.getMainLooper()
                        ).addOnSuccessListener {
                            Log.d(tag, "Request Location Updates Pass.")
                        }.addOnFailureListener {
                            Log.e(tag, "Request Location Updates Fail.")
                        }
                    }

//                    updateUI()
                }
                .addOnFailureListener(activity) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(tag, "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i(tag, "PendingIntent unable to execute request.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e(tag, errorMessage)
                            Toast.makeText(activity.baseContext, errorMessage, Toast.LENGTH_LONG).show()
                            mRequestingLocationUpdates = false
                        }
                    }

                    //updateUI()
                }
    }

    fun requestPermissions(activity: MapsActivity) {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(tag, "Displaying permission rationale to provide additional context.")
            //TODO provide permission Rationale
        } else {
            Log.i(tag, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }


    fun stopLocationUpdates(activity: MapsActivity) {
        if (!mRequestingLocationUpdates) {
            Log.d(tag, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                //super.onLocationResult(p0)
                updateUI(activity, p0?.locations)
            }
        })
                .addOnCompleteListener(activity) {
                    mRequestingLocationUpdates = false
                    //setButtonsEnabledState();
                }


    }


}

