package edu.gwu.metroexplorer.async

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import edu.gwu.metroexplorer.views.MapsActivity
import edu.gwu.metroexplorer.model.Station
import org.jetbrains.anko.doAsync

/**
 * Created by cmahajan on 9/23/17.
 */

private const val WMATA_URL = "https://api.wmata.com/Rail.svc/json/jStations"
private const val API_KEY = "64a9e1ee1a3f4d71bb83a451a0452c85"

class FetchMetroStationsAsyncTask {

    interface OnFetchMetroStationsCompletionListener {

        fun onFetchComplete(response: JsonArray)

    }

    fun execute(activity: MapsActivity, listener: OnFetchMetroStationsCompletionListener) {

        doAsync {

            var jsonObj: JsonObject? = null

            val sharedPref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
            val wmataApiKey: String = sharedPref.getString("wmataApiKey", "")

            jsonObj = if(wmataApiKey == null || wmataApiKey.isEmpty()) {

                Ion.with(activity.baseContext)
                        .load(WMATA_URL)
                        .addHeader("api_key", API_KEY)
                        .asJsonObject().get()

            }else{

                Ion.with(activity.baseContext)
                        .load(WMATA_URL)
                        .addHeader("api_key", wmataApiKey)
                        .asJsonObject().get()

            }

            val editor : SharedPreferences.Editor = sharedPref.edit()
            editor.putString("wmataApiKey", API_KEY)
            editor.commit()
            readMessagesArray(jsonObj.getAsJsonArray("Stations"))
            listener.onFetchComplete(jsonObj.getAsJsonArray("Stations"))

        }

    }

    private fun readMessagesArray(stationsArray: JsonArray?): List<Station> {
        val items = mutableListOf<Station>()
        val gson = GsonBuilder().setPrettyPrinting().create() // for pretty print feature

        stationsArray?.mapTo(items) { gson.fromJson(it, Station::class.java) }
        return items
    }


}