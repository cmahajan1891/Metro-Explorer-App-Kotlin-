package edu.gwu.metroexplorer.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import edu.gwu.metroexplorer.R
import kotlinx.android.synthetic.main.activity_landmarks.*

class LandmarksActivity : AppCompatActivity() {

    private lateinit var landMarkRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)


        landMarkRecyclerView = landmarks_recycler_view
    }
}
