package com.intellyticshub.projectmyoffers.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


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
)