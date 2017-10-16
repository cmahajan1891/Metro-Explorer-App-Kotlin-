package edu.gwu.metroexplorer.views


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.gwu.metroexplorer.R
import edu.gwu.metroexplorer.async.FetchLandmarksAsyncTask
import edu.gwu.metroexplorer.model.YelpLandmark
import kotlinx.android.synthetic.main.activity_landmarks.*


class LandmarksActivity : AppCompatActivity() {

    private lateinit var fetchLandmarksTask: FetchLandmarksAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        // use a linear layout manager
        landmarksRecyclerView.layoutManager = LinearLayoutManager(this)
        var lat = getIntent().getDoubleExtra(getString(R.string.lat), 91.0)
        var lon = getIntent().getDoubleExtra(getString(R.string.lon), 181.0)

        val listener = object : FetchLandmarksAsyncTask.OnFetchLandmarksCompletionListener {

            override fun onFetchComplete(response: Array<YelpLandmark>?) {
                runOnUiThread {
                    var landmarksAdapter = LandmarksAdapter(response, this@LandmarksActivity)
                    landmarksRecyclerView.adapter = landmarksAdapter
                }
            }
        }

        if (lat > 90 || lon > 180) {

        } else {
            fetchLandmarksTask = FetchLandmarksAsyncTask()
            fetchLandmarksTask.execute(this@LandmarksActivity, lat.toString(),
                    "" + lon.toString(), listener)
        }
    }
}


class LandmarksAdapter(private val dataSet: Array<YelpLandmark>?, private val activity: LandmarksActivity) : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {


    //Array<YelpLandmark>

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val gradientView: ImageView = view.findViewById(R.id.gradientView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val isOpenTextView: TextView = view.findViewById(R.id.isOpenTextView)
        val distance: TextView = view.findViewById(R.id.distance)
    }

    private fun callIntent(position: Int) {
        Log.d("Clickable", "called")
        val intent = Intent(activity, LandmarkDetailActivity::class.java)
        var landmark: YelpLandmark? = dataSet?.get(position)
        intent.putExtra("YELP_LANDMARK", landmark)
        activity.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // create a new view
        val detailView = LayoutInflater.from(parent?.context).inflate(R.layout.view_landmark_detail, parent, false);
        return ViewHolder(detailView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        var landmark: YelpLandmark? = dataSet?.get(position)
        Picasso.with(activity).load(landmark?.imageURL).into(holder?.imageView)
        holder?.imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
        holder?.nameTextView?.text = landmark?.name
        holder?.ratingBar?.rating = landmark?.rating!!
        holder?.distance?.text = getDistanceText(landmark?.distance)
        if (landmark?.isClosed) {
            holder?.isOpenTextView?.text = activity.getString(R.string.closed)
            holder?.isOpenTextView?.setTextColor(Color.RED)
        } else {
            holder?.isOpenTextView?.text = activity.getString(R.string.open)
            holder?.isOpenTextView?.setTextColor(Color.GREEN)
        }
        holder?.gradientView?.setOnClickListener({
            callIntent(position)
        })
        holder?.nameTextView?.setOnClickListener({
            callIntent(position)
        })
        holder?.isOpenTextView?.setOnClickListener({
            callIntent(position)
        })
        holder?.ratingBar?.setOnClickListener({
            callIntent(position)
        })
        holder?.distance?.setOnClickListener({
            callIntent(position)
        })

    }

    fun getDistanceText(distance: Double): String {
        var miles: Double = distance / 1609.344
        return "%.2f".format(miles) + " Miles"
    }

    override fun getItemCount(): Int {
        if (dataSet == null)
            return 0
        return dataSet.count()
    }
}


