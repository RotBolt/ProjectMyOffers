package com.intellyticshub.projectmyoffers.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Offers")
data class OfferModel(
    @PrimaryKey
    val offerCode: String,
    val offer: String,
    val vendor: String,
    val expiryDate: String,
    val message: String,
    val expiryTimeInMillis: Long,
    var deleteMark: Boolean = false
) : Parcelable