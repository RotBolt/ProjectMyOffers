package com.intellyticshub.projectmyoffers.utils

import android.app.Application
import android.content.Context
import androidx.work.*
import com.intellyticshub.projectmyoffers.data.Repository
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import java.util.*
import java.util.concurrent.TimeUnit

class ExpiredOfferMarker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val repository = Repository.getInstance(applicationContext as Application)
        val allOffers = repository.allOffers
        val marked = ArrayList<OfferModel>()
        if (allOffers != null) {
            val currTimeMillis = System.currentTimeMillis()
            for (offer in allOffers) {
                if (offer.expiryTimeInMillis < currTimeMillis && !offer.deleteMark) {
                    offer.deleteMark = true
                    offer.message = ""
                    marked.add(offer)
                }
            }
            if (marked.isNotEmpty()) {
                repository.updateOffers(*marked.toTypedArray())
                scheduleExpiryDeleting(Constants.deleteExpiryDays)
            }
        }
        return Result.success()
    }
}


fun scheduleExpiryMarking() {
    val expiryCheckBuilder =
        PeriodicWorkRequest.Builder(ExpiredOfferMarker::class.java, 24, TimeUnit.HOURS)

    val expiryCheckWork = expiryCheckBuilder.build()
    WorkManager.getInstance().enqueue(expiryCheckWork)
}


class ExpiredOfferDeleter(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val repository = Repository.getInstance(applicationContext as Application)
        val expiredOffers = repository.markedDeleteOffers
        if (expiredOffers != null && !expiredOffers.isEmpty())
            repository.deleteOffers(*expiredOffers.toTypedArray())
        return Result.success()
    }

}

fun scheduleExpiryDeleting(duration: Long) {
    val deleteWork = OneTimeWorkRequest.Builder(ExpiredOfferDeleter::class.java)
        .setInitialDelay(duration, TimeUnit.DAYS).build()
    WorkManager.getInstance().enqueue(deleteWork)
}

