package edu.gwu.metroexplorer.views

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import edu.gwu.metroexplorer.R
import kotlinx.android.synthetic.main.activity_landmarks.*
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import edu.gwu.metroexplorer.model.StationData


class LandmarksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // use a linear layout manager
        landmarksRecyclerView.layoutManager = LinearLayoutManager(this);

        val samples = arrayOf("January", "February", "March")
        var landmarksAdapter = LandmarksAdapter(samples, this@LandmarksActivity)
        landmarksRecyclerView.adapter = landmarksAdapter
    }
}

class LandmarksAdapter(private val dataSet: Array<String>, private val landmarksActivity: LandmarksActivity) : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

    //Array<YelpLandmark>

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val gradientView: ImageView = view.findViewById(R.id.gradientView)
        val textView4: TextView = view.findViewById(R.id.textView4)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val isOpenTextView: TextView = view.findViewById(R.id.isOpenTextView)
        val distance: TextView = view.findViewById(R.id.distance)
    }

    private fun callIntent(){
        Log.d("Clickable", "called")
        val intent = Intent(landmarksActivity, LandmarkDetailActivity::class.java)

        val samples = arrayOf("January", "February", "March")
        intent.putExtra("yelpDataSet", samples)
        landmarksActivity.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // create a new view
        val detailView = LayoutInflater.from(parent?.context).inflate(R.layout.view_landmark_detail, parent, false);
        return ViewHolder(detailView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //TODO
    }

    override fun getItemCount(): Int {
        return dataSet.count()
    }
}


