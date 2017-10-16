package edu.gwu.metroexplorer.async

import android.content.Context
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import org.jetbrains.anko.doAsync
import android.content.SharedPreferences
import edu.gwu.metroexplorer.views.LandmarksActivity


private const val YELP_AUTH_URL = "https://api.yelp.com/oauth2/token"
private const val YELP_CLIENT_ID = "R-i4F_rOYAMipGlvlP7A-Q"
private const val YELP_CLIENT_SECERET = "zeypXcxuNWa0htU7dDfsEDbYywNrKwxASM1QQxgQZewRbEYQojxYba7mttwd8iDz"
private const val YELP_GRANT_TYPE = "client_credentials"

/**
 * Created by ed-abe on 9/23/17.
 */

class YelpAuthAsyncTask {

    interface OnAuthListener {

        fun onAuth(accessToken: String)
    }
    fun execute(activity: LandmarksActivity, listener: OnAuthListener) {

        doAsync {
            var jsonObj : JsonObject = Ion.with(activity.baseContext)
                                        .load(YELP_AUTH_URL)
                                        .setBodyParameter("client_id", YELP_CLIENT_ID)
                                        .setBodyParameter("client_secret", YELP_CLIENT_SECERET)
                                        .setBodyParameter("grant_type", YELP_GRANT_TYPE)
                                        .asJsonObject()
                                        .get()
            val accessToken : String = jsonObj.get("access_token").asString
            val sharedPref : SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPref.edit()
            editor.putString("yelp_access_token", accessToken)
            editor.commit()
            listener.onAuth(accessToken)
        }

    }
}