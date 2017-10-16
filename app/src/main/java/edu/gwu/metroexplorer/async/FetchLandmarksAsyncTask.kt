package edu.gwu.metroexplorer.async

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import edu.gwu.metroexplorer.model.YelpLandmark
import edu.gwu.metroexplorer.model.YelpSearch
import edu.gwu.metroexplorer.views.LandmarksActivity
import org.jetbrains.anko.doAsync


/**
 * Created by ed-abe on 9/24/17.
 */


private const val YELP_SEARCH_URL = "https://api.yelp.com/v3/businesses/search"

class FetchLandmarksAsyncTask {

    interface OnFetchLandmarksCompletionListener {

        fun onFetchComplete(response: Array<YelpLandmark?>)
    }

    fun execute(activity: LandmarksActivity, latitude: String, longitude: String, listener: OnFetchLandmarksCompletionListener) {

        val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val accessToken: String = sharedPref.getString("yelp_access_token", "")

        if (accessToken == null || accessToken.length == 0) {
            var yelpAuthTask: YelpAuthAsyncTask = YelpAuthAsyncTask()
            val authListner = object : YelpAuthAsyncTask.OnAuthListener {
                override fun onAuth(accessToken: String) {
                    getLandmarks(activity, accessToken, latitude, longitude, listener)
                }
            }
            yelpAuthTask.execute(activity, authListner)
        } else {
            getLandmarks(activity, accessToken, latitude, longitude, listener)
        }

    }

    fun getLandmarks(activity: LandmarksActivity, accessToken: String, latitude: String, longitude: String, listener: OnFetchLandmarksCompletionListener) {

        doAsync {
            var url = YELP_SEARCH_URL + "?latitude=" + latitude + "&longitude=" + longitude + "&radius_filter=8000"
            var jsonObj: JsonObject = Ion.with(activity.baseContext)
                    .load(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .asJsonObject()
                    .get()
            val gson = Gson()

            if (jsonObj != null) {
                var search: YelpSearch = gson.fromJson(jsonObj, YelpSearch::class.java)
                listener.onFetchComplete(search.businesses)
            } else {
                listener.onFetchComplete(emptyArray())
            }

        }
    }
}