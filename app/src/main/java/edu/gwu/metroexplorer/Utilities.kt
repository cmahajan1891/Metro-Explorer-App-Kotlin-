package edu.gwu.metroexplorer

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion


/**
 * Created by cmahajan on 9/23/17.
 */
private const val WMATA_URL = "https://api.wmata.com/Rail.svc/json/jStations"
private const val API_KEY = "e1eee2b5677f408da40af8480a5fd5a8"

object Utilities {

    fun getStationData(context: Context) {
        var jsonObj = getJSONData(context)
        val items = readJsonStream(jsonObj)
    }


    fun readJsonStream(obj: JsonObject): List<Station> {
        val stationsArray = obj.getAsJsonArray("Stations")
        try {
            return readMessagesArray(stationsArray)
        } finally {

        }
    }

    private fun readMessagesArray(stationsArray: JsonArray?): List<Station> {
        val items = mutableListOf<Station>()
        val gson = GsonBuilder().setPrettyPrinting().create() // for pretty print feature

        stationsArray?.mapTo(items) { gson.fromJson(it, Station::class.java) }
        return items
    }

    private fun getJSONData(context: Context): JsonObject {

        return Ion.with(context)
                .load(WMATA_URL)
                .addHeader("api_key", API_KEY)
                .asJsonObject().get()

    }

}