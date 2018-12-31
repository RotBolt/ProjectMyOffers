package com.intellyticshub.projectmyoffers.ui.actvities

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
import kotlinx.android.synthetic.main.activity_main.*

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
        Log.i("PUI", "startLoading")
        viewPager.visibility = View.GONE
        progress_circular.visibility = View.VISIBLE
        progress_circular.animate()

    }

    private fun stopLoadingAnim() {
        Log.i("PUI", "stopLoading")
        viewPager.visibility = View.VISIBLE
        progress_circular.visibility = View.GONE
    }


    private fun scanForOffers() {
        startLoadingAnim()
        Handler().postDelayed(
            { stopLoadingAnim() },
            2500
        )
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