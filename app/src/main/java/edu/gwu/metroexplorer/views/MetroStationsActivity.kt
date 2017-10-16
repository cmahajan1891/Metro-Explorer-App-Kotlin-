package edu.gwu.metroexplorer.views

import android.app.SearchManager
import android.content.Context
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

        var ls = object : FetchMetroStationsAsyncTask.OnFetchMetroStationsCompletionListener {
            override fun onFetchComplete(response: List<Station>) {
                runOnUiThread {
                    stationData = StationData(response)

                    // specify an adapter (see also next example)


                    var findClosest = intent.getBooleanExtra("findClosest", false)

                    var metroAdapter = if (findClosest) {
                        val items = mutableListOf<Station>()
                        var lat = intent.getDoubleExtra("lat", 0.0)
                        var long = intent.getDoubleExtra("long", 0.0)


                        stationData.stations.forEach {

                            var lat2 = it.Lat.toDouble()
                            var lon2 = it.Lon.toDouble()

                            val res = FloatArray(1)

                            android.location.Location.distanceBetween(lat, long, lat2, lon2, res)

                            Log.d("Distance", res[0].toString())
                            if (res[0] / 1000 <= 2.0) {
                                items.add(it)
                            }

                        }
                        MetroAdapter(StationData(items), this@MetroStationsActivity)
                    } else {
                        MetroAdapter(stationData, this@MetroStationsActivity)
                    }

                    metroRecyclerView.adapter = metroAdapter


                }
            }

        }

        fetchMetroStationAsyncTask = FetchMetroStationsAsyncTask()
        fetchMetroStationAsyncTask.execute(this@MetroStationsActivity, ls)

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

        //Associate searchable configuration with the SearchView
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.filter)?.actionView as SearchView

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                //Log.d("onQueryTextChangeListener", "called.")


                val onCmpLst = object : onCompleteListener {
                    override fun onComplete() {
                        showLoading(false)
                    }
                }
                handleIntent(stationData, onCmpLst, newText)

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                //Log.d("onQueryTextSubmitListener", "called.")

                val onCmpLst = object : onCompleteListener {
                    override fun onComplete() {
                        showLoading(false)
                    }
                }
                handleIntent(stationData, onCmpLst, query)
                return true
            }

        })



        return true
    }

    private fun handleIntent(stData: StationData, onCmpLst: onCompleteListener, changedText: String?) {
        runOnUiThread {
            //stuff that updates ui
            //use the query to search your data somehow
            showLoading(true)
            //val query = intent?.getStringExtra(SearchManager.QUERY)
            val items = mutableListOf<Station>()

            Log.d("query string is: ", changedText)
            stData.stations.forEach {
                Log.d("Station: ", it.Name)

                if (changedText != null) {

                    if (it.Name.capitalize().startsWith(changedText.capitalize())) {
                        items.add(it)
                    }

                }

            }
            val stationData = StationData(items)

            var metroAdapter = MetroAdapter(stationData, this@MetroStationsActivity)
            metroRecyclerView.adapter = metroAdapter
            metroRecyclerView.adapter.notifyDataSetChanged()
            onCmpLst.onComplete()

        }
    }


}
