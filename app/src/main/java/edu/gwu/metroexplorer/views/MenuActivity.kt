package edu.gwu.metroexplorer.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.async.FetchLandmarksAsyncTask
import edu.gwu.metroexplorer.async.FetchMetroStationsAsyncTask
import edu.gwu.metroexplorer.location.*
import edu.gwu.metroexplorer.model.Station
import edu.gwu.metroexplorer.model.StationData
import edu.gwu.metroexplorer.model.YelpLandmark
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {

    private lateinit var locationDetector: LocationDetector
    private lateinit var fetchMetroStationAsyncTask: FetchMetroStationsAsyncTask
//    private lateinit var locCallback: LocationCallback
    private lateinit var metroDataSet: StationData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val ls = object : FetchMetroStationsAsyncTask.OnFetchMetroStationsCompletionListener {
            override fun onFetchComplete(response: List<Station>) {
                Log.d("MetroDataSet", "Populated")
                metroDataSet = StationData(response)
            }
        }

        locationDetector = LocationDetector()

        locationDetector.mRequestingLocationUpdates = false
        locationDetector.updateValuesFromBundle(savedInstanceState, this)

        locationDetector.getFusedLocationClient(this)
        locationDetector.createLocationRequest()
        locationDetector.buildLocationSettingsRequest()

        fetchMetroStationAsyncTask = FetchMetroStationsAsyncTask()
        fetchMetroStationAsyncTask.execute(this@MenuActivity, ls)


        closestStation.setOnClickListener {
            val intent = Intent(this@MenuActivity, MetroStationsActivity::class.java)

            intent.putExtra(
                    "metroDataSet",
                    metroDataSet
            )
            intent.putExtra("lat", locationDetector.mCurrentLocation?.latitude)
            intent.putExtra("long", locationDetector.mCurrentLocation?.longitude)
            intent.putExtra("findClosest", true)
            startActivity(intent)
        }

        favoriteLandMark.setOnClickListener {
            val intent = Intent(this@MenuActivity, LandmarksActivity::class.java)

            intent.putExtra(getString(R.string.lat), 38.900647)
            intent.putExtra(getString(R.string.lon),  -77.050370)
            startActivity(intent)
        }

        selectStation.setOnClickListener {
            val intent = Intent(this@MenuActivity, MetroStationsActivity::class.java)

            intent.putExtra(
                    "metroDataSet",
                    metroDataSet
            )
            intent.putExtra("findClosest", false)
            startActivity(intent)
        }

    }

    override fun onResume() {
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (locationDetector.isReady()) {
            if (locationDetector.mRequestingLocationUpdates && locationDetector.checkPermissions(this@MenuActivity.applicationContext)) {
//                locationDetector.startLocationUpdates(this, locCallback)
            } else if (!locationDetector.checkPermissions(this@MenuActivity.applicationContext)) {
                locationDetector.requestPermissions(this@MenuActivity)
                //return
            }
        }
        super.onResume()
    }

    override fun onPause() {
        // Remove location updates to save battery.
        if (locationDetector.isReady()) {
//            locationDetector.stopLocationUpdates(this, locCallback)
        }
        super.onPause()
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        Log.i(locationDetector.TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(locationDetector.TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationDetector.mRequestingLocationUpdates) {
                    Log.i(locationDetector.TAG, "Permission granted, updates requested, starting location updates")
//                    locationDetector.startLocationUpdates(this, locCallback)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(locationDetector.TAG, "User agreed to make required location settings changes.")
                    locationDetector.mRequestingLocationUpdates = true
                }
                Activity.RESULT_CANCELED -> {
                    Log.i(locationDetector.TAG, "User chose not to make required location settings changes.")
                    locationDetector.mRequestingLocationUpdates = false
                    locationDetector.updateUI(this, listOf(locationDetector.mCurrentLocation) as List<Location>)
                }

            }// Nothing to do. startLocationupdates() gets called in onResume again.
        }
    }


}
