package com.intellyticshub.projectmyoffers.ui.actvities

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.data.Repository
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import com.intellyticshub.projectmyoffers.ui.adapters.PagerAdapter
import com.intellyticshub.projectmyoffers.utils.OfferExtractor
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    var isActiveTab: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFields()
    }

    private fun initFields() {
        progress_circular.visibility = View.GONE
        viewPager.adapter = PagerAdapter(supportFragmentManager, 2)
        viewPager.offscreenPageLimit = 1
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        isActiveTab = true
                        viewPager.currentItem = 0
                        fab.setImageResource(R.drawable.ic_find)
                    }
                    1 -> {
                        isActiveTab = false
                        viewPager.currentItem = 1
                        fab.setImageResource(R.drawable.ic_delete)
                    }
                }
            }
        })

        fab.setOnClickListener {
            if (isActiveTab) {
                scanForOffers()
            } else {
                deleteAllExpired()
            }
        }
    }

    private fun startLoadingAnim() {
        viewPager.visibility = View.GONE
        progress_circular.visibility = View.VISIBLE
        progress_circular.animate()

    }

    private fun stopLoadingAnim() {
        viewPager.visibility = View.VISIBLE
        progress_circular.visibility = View.GONE
    }


    private fun scanForOffers() {
        startLoadingAnim()

        val repository = Repository.getInstance(application)


        val newOffers = ArrayList<OfferModel>()
        val executor = Executors.newSingleThreadExecutor()
        val scanTask = Callable {

            val smsURI = Uri.parse("content://sms/inbox")
            val cursor = contentResolver.query(
                smsURI, null, null, null, null
            )

            cursor?.run {
                while (moveToNext()) {
                    val address = getString(getColumnIndexOrThrow("address"))
                    val smsBody = getString(getColumnIndexOrThrow("body"))
                    val timeInMillis = getLong(getColumnIndexOrThrow("date"))
                    val offerExtractor = OfferExtractor(smsBody)
                    val offerCode = offerExtractor.extractOfferCode()
                    val offer = offerExtractor.extractOffer()
                    if (offerCode != "none" && offer != "none") {
                        val calendar = Calendar.getInstance().apply { setTimeInMillis(timeInMillis) }
                        val smsYear = calendar.get(Calendar.YEAR).toString()

                        val expiryDateInfo = offerExtractor.extractExpiryDate(smsYear)

                        val expiryDate = when {
                            expiryDateInfo.first == "last day" || expiryDateInfo.first == "expiring today" -> with(
                                calendar
                            ) {
                                "${get(Calendar.DAY_OF_MONTH)}-${get(Calendar.MONTH) + 1}-${get(Calendar.YEAR)}"
                            }
                            expiryDateInfo.first == "none" -> ""
                            else -> expiryDateInfo.first
                        }

                        val expiryTimeMillis = if (expiryDateInfo.second == -2L) timeInMillis else expiryDateInfo.second

                        val newOffer = OfferModel(
                            offerCode = offerCode,
                            offer = offer,
                            vendor = address,
                            message = smsBody,
                            expiryDate = expiryDate,
                            expiryTimeInMillis = expiryTimeMillis,
                            deleteMark = false
                        )

                        if (expiryDateInfo.second == -2L) {
                            Log.i(
                                "PUI", """
                                expirydate $expiryDate
                                expiryTime $expiryTimeMillis

                                model $newOffer
                            """.trimIndent()
                            )
                        }
                        newOffers.add(newOffer)
                    }
                }
            }
            cursor?.close()
            repository.insertOffers(*newOffers.toTypedArray())
        }

        Handler().post {
            executor.submit(scanTask).get()
            stopLoadingAnim()
        }
    }

    private fun deleteAllExpired() {
        val repository = Repository.getInstance(application)
        var expiredList: List<OfferModel> = listOf()
        val expiredLive = repository.expiredOffers
        expiredLive.observe(this, Observer { it -> it.let { expiredList = it } })
        expiredLive.removeObservers(this)
        repository.deleteOffers(*(expiredList.toTypedArray()))
    }
}