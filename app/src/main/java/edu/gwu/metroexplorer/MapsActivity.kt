package edu.gwu.metroexplorer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_menu.*
import java.text.DateFormat
import android.provider.Settings
import java.util.*


private const val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
private const val KEY_LOCATION = "location"
private const val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"
private const val REQUEST_CHECK_SETTINGS = 0x1

private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
private val tag = MapsActivity::class.java.simpleName
/**
 * The fastest rate for active location updates. Exact. Updates will never be more frequent
 * than this value.
 */
private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mRequestingLocationUpdates: Boolean = true
    private var mCurrentLocation: Location? = null
    private var mLastUpdateTime: String = ""

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    /**
     * Callback for Location events.
     */
    private lateinit var mLocationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Initializing the mRequesting Location Updates variable to false;
        mRequestingLocationUpdates = true

        updateValuesFromBundle(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        closestStation.setOnClickListener {
            //TODO
        }

        favoriteLandMark.setOnClickListener {
            //TODO
        }

        selectStation.setOnClickListener {
            //TODO
        }

    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())

                updateLocationUI()
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
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
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

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
            updateUI()
        }
    }

    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.i(tag, "User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.i(tag, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
                    updateUI()
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }

//    /**
//     * Handles the Start Updates button and requests start of location updates. Does nothing if
//     * updates have already been requested.
//     */
//    fun startUpdatesButtonHandler(view: View) {
//        if (!mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = true
//            //setButtonsEnabledState()
//            startLocationUpdates()
//        }
//    }
//
//    /**
//     * Handles the Stop Updates button, and requests removal of location updates.
//     */
//    fun stopUpdatesButtonHandler(view: View) {
//        // It is a good practice to remove location requests when the activity is in a paused or
//        // stopped state. Doing so helps battery performance and is especially
//        // recommended in applications that request frequent location updates.
//        stopLocationUpdates()
//    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */

    //TODO implement the Permissions Logic

    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this) {
                    Log.i(tag, "All location settings are satisfied.")
                    if (checkSelfPermission(this.baseContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper())
                    }

                    updateUI()
                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(tag, "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i(tag, "PendingIntent unable to execute request.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e(tag, errorMessage)
                            Toast.makeText(this.baseContext, errorMessage, Toast.LENGTH_LONG).show()
                            mRequestingLocationUpdates = false
                        }
                    }

                    updateUI()
                }
    }


    private fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(tag, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this) {
                    mRequestingLocationUpdates = false
                    //setButtonsEnabledState();
                }


    }

    override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates()
        } else if (!checkPermissions()) {
            requestPermissions()
        }

        updateUI()
    }

    override fun onPause() {
        super.onPause()

        // Remove location updates to save battery.
        stopLocationUpdates()
    }

    /**
     * Stores activity data in the Bundle.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates)
        savedInstanceState?.putParcelable(KEY_LOCATION, mCurrentLocation)
        savedInstanceState?.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime)
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = checkSelfPermission(this.baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(
                findViewById<View>(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show()
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(tag, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {
            Log.i(tag, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        Log.i(tag, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(tag, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(tag, "Permission granted, updates requested, starting location updates")
                    startLocationUpdates()
                }
            } else {
                // Permission denied.
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.
                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, View.OnClickListener {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()

                    val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                    intent.action =
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
            }
        }
    }


    /**
     * Updates all UI fields.
     */
    private fun updateUI() {
        //setButtonsEnabledState()
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (mCurrentLocation!=null){
            val lng = mCurrentLocation!!.longitude.toString()
            val lat = mCurrentLocation!!.latitude.toString()
            val lstTime = mLastUpdateTime
            //mMap.addMarker()
            Toast.makeText(this.baseContext, "Latitude: $lat Longitude: $lng Last Updated Time: $lstTime", Toast.LENGTH_SHORT)
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateLocationUI()
        //locationDetector.getLastKnownLocation(this, mMap)
        //val loc = locationDetector.latLng


    }

}
