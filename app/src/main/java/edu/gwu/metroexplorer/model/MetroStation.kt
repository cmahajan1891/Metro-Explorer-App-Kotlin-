package edu.gwu.metroexplorer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ed-abe on 10/2/17.
 */


@Parcelize
data class MetroStation(val latitude: Double,
                        val longitude: Double,
                        val name: String,
                        val description: String,
                        val address: String): Parcelable