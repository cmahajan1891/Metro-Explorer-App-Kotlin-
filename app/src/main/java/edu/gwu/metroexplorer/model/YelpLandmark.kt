package edu.gwu.metroexplorer.model

import com.google.gson.annotations.SerializedName


/**
 * Created by ed-abe on 10/2/17.
 */

data class YelpCoordinates(@SerializedName("latitude") val latitude: String,
                           @SerializedName("longitude") val longitude: Double)

data class YelpSearch(@SerializedName("businesses") val businesses: ArrayList<YelpLandmark>)

data class YelpLandmark(@SerializedName("coordinates") val coordinates: YelpCoordinates,
                        @SerializedName("name")  val name: String,
                        @SerializedName("address")  val address: String,
                        @SerializedName("is_closed") val isClosed: Boolean,
                        @SerializedName("image_url") val imageURL: String,
                        @SerializedName("url") val yelpURL: String)
