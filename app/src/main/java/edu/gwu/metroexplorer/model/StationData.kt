package edu.gwu.metroexplorer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by cmahajan on 10/6/17.
 */
@Parcelize
data class StationData(val stations: List<Station>) : Parcelable