package edu.gwu.metroexplorer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Created by ed-abe on 10/2/17.
 */

@Parcelize
data class YelpCoordinates(@SerializedName("latitude") val latitude: String,
                           @SerializedName("longitude") val longitude: Double) : Parcelable

@Parcelize
data class YelpSearch(@SerializedName("businesses") val businesses: Array<YelpLandmark>) : Parcelable

@Parcelize
data class YelpLandmark(@SerializedName("coordinates") val coordinates: YelpCoordinates,
                        @SerializedName("name") val name: String,
                        @SerializedName("rating") val rating: Float,
                        @SerializedName("distance") val distance: Double,
                        @SerializedName("address") val address: String,
                        @SerializedName("is_closed") val isClosed: Boolean,
                        @SerializedName("image_url") val imageURL: String,
                        @SerializedName("url") val yelpURL: String) : Parcelable
