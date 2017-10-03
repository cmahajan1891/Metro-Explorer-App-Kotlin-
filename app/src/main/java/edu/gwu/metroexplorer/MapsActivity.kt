package edu.gwu.metroexplorer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_menu.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationDetector: LocationDetector
    private lateinit var mMap: GoogleMap
    private lateinit var fetchMetroStationAsyncTask: FetchMetroStationsAsyncTask
    private lateinit var fetchLandmarksTask: FetchLandmarksAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val listner = object : FetchLandmarksAsyncTask.OnFetchLandmarksCompletionListener {
            override fun onFetchComplete(response: JsonArray) {
                print(response)
            }
        }
        val ls = object : FetchMetroStationsAsyncTask.OnFetchMetroStationsCompletionListener {
            override fun onFetchComplete(response: JsonArray) {
                print(response)
            }
        }

        fetchMetroStationAsyncTask = FetchMetroStationsAsyncTask()
        fetchMetroStationAsyncTask.execute(this,ls)

        fetchLandmarksTask = FetchLandmarksAsyncTask()
        fetchLandmarksTask.execute(this, "" + 38.896841, "" + -77.050110, listner)

        locationDetector = LocationDetector()
        locationDetector.getLastKnownLocation(this)

        locationDetector.updateValuesFromBundle(savedInstanceState, this)
        locationDetector.createLocationRequest()
        locationDetector.buildLocationSettingsRequest()

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

    override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (locationDetector.mRequestingLocationUpdates && PermissionChecker.checkSelfPermission(this@MapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationDetector.startLocationUpdates(this)
        } else if (PermissionChecker.checkSelfPermission(this@MapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationDetector.requestPermissions(this)
        }
    }

    override fun onPause() {
        super.onPause()
        // Remove location updates to save battery.
        locationDetector.stopLocationUpdates(this)
    }

    override fun onDestroy() {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("yelp_access_token", "")
        editor.putString("wmataApiKey", "")
        editor.commit()
        super.onDestroy()
    }

    /**
     * Stores activity data in the Bundle.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, locationDetector.mRequestingLocationUpdates)
        savedInstanceState?.putParcelable(KEY_LOCATION, locationDetector.mCurrentLocation)
        super.onSaveInstanceState(savedInstanceState)
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
        //updateLocationUI()
        //locationDetector.getLastKnownLocation(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.i(locationDetector.tag, "User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.i(locationDetector.tag, "User chose not to make required location settings changes.")
                    locationDetector.mRequestingLocationUpdates = false
                    locationDetector.updateUI(this, arrayListOf(locationDetector.mCurrentLocation!!))
                }
            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        Log.i(locationDetector.tag, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(locationDetector.tag, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationDetector.mRequestingLocationUpdates) {
                    Log.i(locationDetector.tag, "Permission granted, updates requested, starting location updates")
                    locationDetector.startLocationUpdates(this)
                }
            } else {
                //TODO Implement the Snackbar functionality
                // Permission denied.
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.
                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
//                locationDetector.showSnackbar(R.string.permission_denied_explanation,
//                        R.string.settings, View.OnClickListener {
//                    // Build intent that displays the App settings screen.
//                    val intent = Intent()
//
//                    val uri = Uri.fromParts("package",
//                            BuildConfig.APPLICATION_ID, null)
//                    intent.action =
//                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                    intent.data = uri
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                })
            }
        }
    }


}
