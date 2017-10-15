package edu.gwu.metroexplorer.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import edu.gwu.metroexplorer.R
import kotlinx.android.synthetic.main.activity_landmarks.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class LandmarksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // use a linear layout manager
        landmarksRecyclerView.layoutManager = LinearLayoutManager(this);

        val samples = arrayOf("January", "February", "March")
        var landmarksAdapter = LandmarksAdapter(samples)
        landmarksRecyclerView.adapter = landmarksAdapter
    }
}

class LandmarksAdapter(private val dataSet: Array<String>) : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

    //Array<YelpLandmark>

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
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


