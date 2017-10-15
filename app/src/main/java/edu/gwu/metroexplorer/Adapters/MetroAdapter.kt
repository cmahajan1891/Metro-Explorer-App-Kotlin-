package edu.gwu.metroexplorer.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.model.StationData

/**
 * Created by cmahajan on 10/6/17.
 */
class MetroAdapter(private val metroDataSet: StationData) : RecyclerView.Adapter<MetroAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return metroDataSet.stations.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.stationNameView?.text = metroDataSet.stations[position].Name
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.metro_station_text_view, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
         val stationNameView: TextView = view.findViewById(R.id.StationName)
    }

}