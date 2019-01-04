package com.intellyticshub.projectmyoffers.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun copyToClipboard(offerCode: String, context: Context?) {
    val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("OfferModel Code", offerCode)
    clipboard.primaryClip = clip
    Toast.makeText(context, "$offerCode copied", Toast.LENGTH_SHORT).show()
}