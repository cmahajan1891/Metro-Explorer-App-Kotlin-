package edu.gwu.metroexplorer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import org.jetbrains.anko.doAsync
import javax.security.auth.callback.Callback

/**
 * Created by ed-abe on 9/24/17.
 */


private const val YELP_SEARCH_URL = "https://api.yelp.com/v3/businesses/search"

class FetchLandmarksAsyncTask {

    interface OnFetchLandmarksCompletionListener {

        fun onFetchComplete(response: JsonArray)
    }

    fun execute(activity: MapsActivity, latitude: String, longitude: String, listener: OnFetchLandmarksCompletionListener) {

        val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val accessToken: String = sharedPref.getString("yelp_access_token", "")

        if(accessToken== null || accessToken.length==0) {
            var yelpAuthTask: YelpAuthAsyncTask = YelpAuthAsyncTask()
            val authListner = object : YelpAuthAsyncTask.OnAuthListener {
                override fun onAuth(accessToken: String) {
                    getLandmarks(activity, accessToken, latitude, longitude, listener)
                }
            }
            yelpAuthTask.execute(activity, authListner)
        }else{
            getLandmarks(activity, accessToken, latitude, longitude, listener)
        }

    }

    fun getLandmarks(activity: MapsActivity, accessToken: String,latitude: String, longitude: String, listener: OnFetchLandmarksCompletionListener){

        doAsync {
            var url = YELP_SEARCH_URL+"?latitude="+latitude+"&longitude="+longitude+"&radius_filter=8000"
            var jsonObj : JsonObject = Ion.with(activity.baseContext)
                    .load(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .asJsonObject()
                    .get()

            val businesses= jsonObj.getAsJsonArray("businesses")

            listener.onFetchComplete(businesses)

        }
    }
}