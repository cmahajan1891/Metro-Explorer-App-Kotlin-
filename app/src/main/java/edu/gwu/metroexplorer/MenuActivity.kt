package edu.gwu.metroexplorer

import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_menu.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng


class MenuActivity : AppCompatActivity() {

    private lateinit var locationDetector: LocationDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        locationDetector = LocationDetector()
        locationDetector.startLocationUpdates(this)

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
}
