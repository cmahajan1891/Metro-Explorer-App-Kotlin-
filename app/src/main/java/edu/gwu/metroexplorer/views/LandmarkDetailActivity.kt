package edu.gwu.metroexplorer.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import edu.gwu.metroexplorer.R
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import android.content.Intent
import android.net.Uri


class LandmarkDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        websiteButton.setOnClickListener {
            val url: String =  "https://www.google.com/maps/dir/?api=1&origin=38.8976411,-77.0526863&&destination=38.896912, -77.050143"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            this.startActivity(intent)
        }

        directionsButton.setOnClickListener {
            val mapURl: String =  "https://www.google.com/maps/dir/?api=1&origin=38.8976411,-77.0526863&&destination=38.896912, -77.050143"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapURl))
            this.startActivity(intent)
        }
    }

}
