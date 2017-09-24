package edu.gwu.metroexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_menu.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationDetector: LocationDetector
    private lateinit var fetchMetroStationAsyncTask: FetchMetroStationsAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationDetector = LocationDetector()
        //locationDetector.getProvider(this)
        //locationDetector.startLocationUpdates(this)
        //locationDetector.getLastLocation(this)

        //fetchMetroStationAsyncTask = FetchMetroStationsAsyncTask()
        //fetchMetroStationAsyncTask.getStations(this)


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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationDetector.getLastKnownLocation(this, mMap)
        //val loc = locationDetector.latLng


    }


}
