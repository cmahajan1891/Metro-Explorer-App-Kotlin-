package edu.gwu.metroexplorer.Adapters

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.model.StationData
import edu.gwu.metroexplorer.views.LandmarksActivity
import edu.gwu.metroexplorer.views.MetroStationsActivity

/**
 * Created by cmahajan on 10/6/17.
 */

private val map: HashMap<String, Int> = HashMap()


class MetroAdapter(private val metroDataSet: StationData, private val metroStationsActivity: MetroStationsActivity) : RecyclerView.Adapter<MetroAdapter.ViewHolder>() {
    init {
        map.put("RD", Color.RED)
        map.put("YL", Color.parseColor("#FFD700"))
        map.put("GR", Color.GREEN)
        map.put("BL", Color.BLUE)
        map.put("OR", Color.parseColor("#FF8C00"))
        map.put("SV", Color.parseColor("#C0C0C0"))
    }

    override fun getItemCount(): Int {
        return metroDataSet.stations.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (position == 0) {
            holder?.layout?.setPadding(0, 160, 0, 0)
        }

        holder?.stationNameView?.text = metroDataSet.stations[position].Name
        holder?.lineView1?.text = metroDataSet.stations[position].LineCode1
        holder?.lineView2?.text = metroDataSet.stations[position].LineCode2
        holder?.lineView3?.text = metroDataSet.stations[position].LineCode3
        if (metroDataSet.stations[position].LineCode1 != null) map[holder?.lineView1?.text.toString()]?.let { holder?.lineView1?.setBackgroundColor(it) }
        if (metroDataSet.stations[position].LineCode2 != null) map[holder?.lineView2?.text.toString()]?.let { holder?.lineView2?.setBackgroundColor(it) }
        if (metroDataSet.stations[position].LineCode3 != null) map[holder?.lineView3?.text.toString()]?.let { holder?.lineView3?.setBackgroundColor(it) }

        holder?.stationNameView?.setOnClickListener {
            callIntent(metroDataSet, position)
        }

        holder?.lineView1?.setOnClickListener({
            callIntent(metroDataSet, position)
        })
        holder?.lineView2?.setOnClickListener({
            callIntent(metroDataSet, position)
        })

        holder?.lineView3?.setOnClickListener({
            callIntent(metroDataSet, position)
        })


    }

    private fun callIntent(metroDataSet:StationData, position:Int){
        Log.d("Clickable", "called")
        val intent = Intent(metroStationsActivity, LandmarksActivity::class.java)
        intent.putExtra("Lat", metroDataSet.stations[position].Lat)
        intent.putExtra("Lon", metroDataSet.stations[position].Lon)
        metroStationsActivity.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.metro_station_text_view, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stationNameView: TextView = view.findViewById(R.id.StationName)

        val lineView1: TextView = view.findViewById(R.id.line1)
        val lineView2: TextView = view.findViewById(R.id.line2)
        val lineView3: TextView = view.findViewById(R.id.line3)
        //val bgColor: TextView = view.findViewById(R.id.StationName)
        val layout: LinearLayout = view.findViewById(R.id.StationView)
    }

}