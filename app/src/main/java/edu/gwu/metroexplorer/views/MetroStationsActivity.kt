package edu.gwu.metroexplorer.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import edu.gwu.metroexplorer.Adapters.MetroAdapter
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.model.StationData
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MetroStationsActivity : AppCompatActivity() {

    private lateinit var metroRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)
        metroRecyclerView = metro_Station_recycler_view

        metroRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        var metroLayoutManager = LinearLayoutManager(this)
        metroRecyclerView.layoutManager = metroLayoutManager

        // specify an adapter (see also next example)
        var metroAdapter = MetroAdapter(intent.getParcelableExtra<StationData>("metroDataSet"))
        metroRecyclerView.adapter = metroAdapter

    }
}
