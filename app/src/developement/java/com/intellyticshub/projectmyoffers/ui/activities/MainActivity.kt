package com.intellyticshub.projectmyoffers.ui.activities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.data.Repository
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import com.intellyticshub.projectmyoffers.ui.adapters.PagerAdapter
import com.intellyticshub.projectmyoffers.utils.DebugLogger
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
        checkFirstRun()
    }

    private fun checkFirstRun() {
        val sharedPrefs = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE)
        val isFirstRun = sharedPrefs.getBoolean(getString(R.string.first_run), true)

        if (isFirstRun) {
            Handler().post { scanForOffers() }
            sharedPrefs.edit().putBoolean(getString(R.string.first_run), false).apply()
        }

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

        val sharedPrefs = getSharedPreferences(getString(R.string.shared_pref_key), Context.MODE_PRIVATE)
        var lastSmsTimeMillis = sharedPrefs.getLong(getString(R.string.last_sms_time_milllis), -1)
        val newOffers = ArrayList<OfferModel>()
        val executor = Executors.newSingleThreadExecutor()
        val scanTask = Callable {
            val smsURI = Uri.parse("content://sms/inbox")
            val cursor = contentResolver.query(
                smsURI, null, null, null, null
            )
            cursor?.run {
                val debugLogger = DebugLogger()
                val logRecorder = StringBuilder()
                var maxTimeMillis = lastSmsTimeMillis
                while (moveToNext()) {
                    val address = getString(getColumnIndexOrThrow("address"))
                    val smsBody = getString(getColumnIndexOrThrow("body"))

                    logRecorder.append(smsBody + "\n")
                    val timeInMillis = getLong(getColumnIndexOrThrow("date"))
                    if (timeInMillis > lastSmsTimeMillis) {
                        val offerExtractor = OfferExtractor(smsBody)
                        val offerCode = offerExtractor.extractOfferCode()
                        val offer = offerExtractor.extractOffer()

                        logRecorder.append("Offer Code $offerCode, offer $offer \n-----------------------\n")
                        if (offerCode != "none" && offer != "none") {
                            val calendar = Calendar.getInstance().apply { setTimeInMillis(timeInMillis) }
                            val smsYear = calendar.get(Calendar.YEAR).toString()

                            val expiryDateInfo = offerExtractor.extractExpiryDate(smsYear)

                            val expiryDate = when {
                                expiryDateInfo.expiryDate == "last day" || expiryDateInfo.expiryDate == "expiring today" -> with(
                                    calendar
                                ) {
                                    val day = get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                                    val month = (get(Calendar.MONTH) + 1).toString().padStart(2, '0')
                                    val year = get(Calendar.YEAR)
                                    "$day/$month/$year"
                                }
                                expiryDateInfo.expiryDate == "none" -> ""
                                else -> expiryDateInfo.expiryDate
                            }

                            val extraPeriod = 16 * 60 * 60 * 1000L
                            val expiryTimeMillis =
                                if (expiryDateInfo.expiryTimeInMillis == -2L) timeInMillis + extraPeriod else expiryDateInfo.expiryTimeInMillis

                            val newOffer = OfferModel(
                                offerCode = offerCode,
                                offer = offer,
                                vendor = address,
                                message = smsBody,
                                expiryDate = expiryDate,
                                expiryTimeInMillis = expiryTimeMillis,
                                deleteMark = false
                            )
                            newOffers.add(newOffer)
                        }
                    }
                    if (maxTimeMillis < timeInMillis)
                        maxTimeMillis = timeInMillis
                }
                lastSmsTimeMillis = maxTimeMillis
                debugLogger.writeLog("ScanOffers.txt", logRecorder.toString())
            }

            cursor?.close()

            if (newOffers.isNotEmpty()) {
                sharedPrefs.edit().putLong(getString(R.string.last_sms_time_milllis), lastSmsTimeMillis).apply()
                repository.insertOffers(*newOffers.toTypedArray())
            }

            if (newOffers.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(this, "No Offers found :(", Toast.LENGTH_SHORT).show()
                }
            }

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