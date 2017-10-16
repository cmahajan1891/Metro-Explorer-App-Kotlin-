package edu.gwu.metroexplorer.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import edu.gwu.metroexplorer.BuildConfig
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.location.*
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {

    lateinit var locationDetector: LocationDetector
    private lateinit var locCallback: LocationCallback

    private val TAG = "MenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        locCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                locationDetector.mCurrentLocation = locationResult.lastLocation
                Toast.makeText(this@MenuActivity, "Latitude: "+ locationDetector.mCurrentLocation?.latitude +"Longitude: "+ locationDetector.mCurrentLocation?.longitude, Toast.LENGTH_LONG).show()
            }
        }

        locationDetector = LocationDetector()

        locationDetector.prepareLocationDetector(this@MenuActivity, savedInstanceState, locCallback)

        closestStation.setOnClickListener {
            val intent = Intent(this@MenuActivity, MetroStationsActivity::class.java)
            intent.putExtra("lat", locationDetector.mCurrentLocation?.latitude)
            intent.putExtra("long", locationDetector.mCurrentLocation?.longitude)
            intent.putExtra("findClosest", true)
            startActivity(intent)
        }

        favoriteLandMark.setOnClickListener {
            val intent = Intent(this@MenuActivity, LandmarksActivity::class.java)
            startActivity(intent)
        }

        selectStation.setOnClickListener {

            val intent = Intent(this@MenuActivity, MetroStationsActivity::class.java)
            intent.putExtra("findClosest", false)
            startActivity(intent)

        }

    }

    override fun onResume() {

        super.onResume()
        if (locationDetector.checkPermissions()) {
            locationDetector.startLocationUpdates()
        } else if (!locationDetector.checkPermissions()) {
            locationDetector.requestPermissions()
        }

        updateUI()
    }

    override fun onPause() {

        super.onPause()
        locationDetector.stopLocationUpdates()
    }

    override fun onDestroy() {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("yelp_access_token", "")
        editor.putString("wmataApiKey", "")
        editor.commit()
        super.onDestroy()
    }

    fun updateUI(){

    }

    /**
     * Stores activity data in the Bundle.
     */
    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState!!.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, locationDetector.mRequestingLocationUpdates!!)
        savedInstanceState.putParcelable(KEY_LOCATION, locationDetector.mCurrentLocation)
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, locationDetector.mLastUpdateTime)
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationDetector.mRequestingLocationUpdates!!) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates")
                    locationDetector.startLocationUpdates()
                }
            } else {
                // TODO: Permission denied.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(TAG, "User agreed to make required location settings changes.")
                    locationDetector.mRequestingLocationUpdates = true
                }
                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "User chose not to make required location settings changes.")
                    locationDetector.mRequestingLocationUpdates = false
//                    locationDetector.updateUI(this, listOf(locationDetector.mCurrentLocation) as List<Location>)
                }

            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }

}
