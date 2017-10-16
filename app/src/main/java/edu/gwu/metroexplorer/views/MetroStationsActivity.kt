package edu.gwu.metroexplorer.views

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.widget.ProgressBar
import android.widget.SearchView
import edu.gwu.metroexplorer.Adapters.MetroAdapter
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.async.FetchMetroStationsAsyncTask
import edu.gwu.metroexplorer.model.Station
import edu.gwu.metroexplorer.model.StationData
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MetroStationsActivity : AppCompatActivity() {

    interface onCompleteListener {
        fun onComplete()
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var stationData: StationData
    private lateinit var metroRecyclerView: RecyclerView
    private lateinit var fetchMetroStationAsyncTask: FetchMetroStationsAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)
        setSupportActionBar(search_bar as Toolbar)
        metroRecyclerView = metro_Station_recycler_view
        progressBar = indeterminateBar as ProgressBar

        metroRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        var metroLayoutManager = LinearLayoutManager(this)
        metroRecyclerView.layoutManager = metroLayoutManager

        // specify an adapter (see also next example)
        if (intent.action != Intent.ACTION_SEARCH) {

            stationData = intent.getParcelableExtra("metroDataSet")
            var findClosest = intent.getBooleanExtra("findClosest", false)

            var metroAdapter = if (findClosest) {
                val items = mutableListOf<Station>()
                val lat = intent.getDoubleExtra("lat", 0.0)
                val long = intent.getDoubleExtra("long", 0.0)
                stationData.stations.forEach {
                    //val dis = distance(0.0, 0.0, it.Lat.toDouble(), it.Lon.toDouble(), "K")
                    //val dis2 = distance(0.0,0.0, lat, long,"K")
//                    val diff = Math.sqrt(Math.pow(Math.abs(lat) - Math.abs(it.Lat.toDouble()), 2.0)
//                            + Math.pow(Math.abs(long) - Math.abs(it.Lon.toDouble()), 2.0))
//                    if (diff < 20) {
//                        items.add(it)
//                    }
                }
                MetroAdapter(StationData(items), this@MetroStationsActivity)
            } else {
                MetroAdapter(stationData, this@MetroStationsActivity)
            }

            metroRecyclerView.adapter = metroAdapter

        } else {

            showLoading(true)


            var ls = object : FetchMetroStationsAsyncTask.OnFetchMetroStationsCompletionListener {
                override fun onFetchComplete(response: List<Station>) {
                    val stData = StationData(response)

                    val onCmpLst = object : onCompleteListener {
                        override fun onComplete() {
                            showLoading(false)
                        }
                    }
                    handleIntent(intent, stData, onCmpLst)

                }
            }

            fetchMetroStationAsyncTask = FetchMetroStationsAsyncTask()
            fetchMetroStationAsyncTask.execute(this@MetroStationsActivity, ls)

        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            progressBar.visibility = ProgressBar.VISIBLE
        } else {
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.filter)?.actionView as SearchView

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        return true
    }

    private fun handleIntent(intent: Intent?, stData: StationData, onCmpLst: onCompleteListener) {
        runOnUiThread {
            //stuff that updates ui
            //use the query to search your data somehow
            if (Intent.ACTION_SEARCH == intent?.action) {
                val query = intent.getStringExtra(SearchManager.QUERY)
                val items = mutableListOf<Station>()
                Log.d("query string is: ", query.toString())
                stData.stations.forEach {
                    Log.d("Station: ", it.Name)
                    //TODO Not working for all the Stations. Correct the filtering.
                    if (it.Name.capitalize().contains(query.capitalize())) {
                        items.add(it)
                    }
                }
                val stationData = StationData(items)
                var metroAdapter = MetroAdapter(stationData, this@MetroStationsActivity)
                metroRecyclerView.adapter = metroAdapter
                //metroRecyclerView.adapter.notifyDataSetChanged()
                onCmpLst.onComplete()
            }
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        if (unit === "K") {
            dist *= 1.609344
        } else if (unit === "N") {
            dist *= 0.8684
        }

        return dist
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }


}
