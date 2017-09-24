package edu.gwu.metroexplorer

import org.jetbrains.anko.doAsync

/**
 * Created by cmahajan on 9/23/17.
 */

class FetchMetroStationsAsyncTask {
    fun getStations(activity: MapsActivity) {
        doAsync {
            Utilities.getStationData(activity.baseContext)
        }
    }
}