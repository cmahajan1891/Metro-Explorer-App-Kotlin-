package edu.gwu.metroexplorer.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.gwu.metroexplorer.R
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import android.widget.ImageView
import com.squareup.picasso.Picasso
import edu.gwu.metroexplorer.model.YelpLandmark
import com.google.gson.Gson
import android.content.Context
import android.content.SharedPreferences
import kotlin.collections.HashMap


class LandmarkDetailActivity : AppCompatActivity() {

    private lateinit var landmark: YelpLandmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        landmark = getIntent().getParcelableExtra("YELP_LANDMARK")
        titleTextView.text = landmark.name
        ratingBar2.rating = landmark.rating

        addressTextView.text = ""//TODO landmark.address

        Picasso.with(this@LandmarkDetailActivity).load(landmark.imageURL).into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.CENTER_CROP

        val favs = getFavorites()

//        favoriteButton.isSelected = landmark != null && favs?.containsKey(landmark.id)!!

        websiteButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(landmark.yelpURL))
            this.startActivity(intent)
        }

        directionsButton.setOnClickListener {
            val mapURl: String = "https://www.google.com/maps/dir/?api=1&origin=38.8976411,-77.0526863&&destination=38.896912, -77.050143"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapURl))
            this.startActivity(intent)
        }

        favoriteButton.setOnClickListener {

            if (favoriteButton.isSelected) {
                removeFavorite(landmark)
            } else {
                addFavorite(landmark)
            }

            favoriteButton.isSelected = !favoriteButton.isSelected

        }
        shareButton.setOnClickListener {
            var shareText : String = landmark.name
            shareText = shareText + "\n" + landmark.yelpURL

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            this.startActivity(intent)


        }
    }

    // This four methods are used for maintaining favorites.
    fun saveFavorites(favorites: HashMap<String, YelpLandmark>) {


        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()

        val gson = Gson()
        val jsonFavorites = gson.toJson(favorites)

        editor.putString("FAVORITES", jsonFavorites)

        editor.commit()
    }

    fun addFavorite(landmark: YelpLandmark) {
        var favorites: HashMap<String, YelpLandmark>? = getFavorites()
        if (favorites == null)
            favorites = HashMap()
        favorites.set(landmark.id, landmark)
        saveFavorites(favorites)
    }

    fun removeFavorite(yelpLandmark: YelpLandmark) {
        val favorites = getFavorites()
        if (favorites != null) {
            favorites.remove(yelpLandmark.id)
            saveFavorites(favorites)
        }
    }

    fun getFavorites(): HashMap<String, YelpLandmark>? {
        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        var favorites: HashMap<String, YelpLandmark>
        if (sharedPref.contains("FAVORITES")) {
            val jsonFavorites = sharedPref.getString("FAVORITES", null)
            val gson = Gson()
            val favoriteItems = gson.fromJson(jsonFavorites,
                    HashMap::class.java)

            favorites = favoriteItems as HashMap<String, YelpLandmark>
        } else
            return null

        return favorites
    }

}
