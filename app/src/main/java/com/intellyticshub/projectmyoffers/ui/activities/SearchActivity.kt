package com.intellyticshub.projectmyoffers.ui.activities

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import com.intellyticshub.projectmyoffers.data.viewModels.ActiveOfferViewModel
import com.intellyticshub.projectmyoffers.ui.adapters.FilteredOffersAdapter
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction
import com.intellyticshub.projectmyoffers.utils.copyToClipboard
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var mViewModel: ActiveOfferViewModel
    private lateinit var offerAdapter: FilteredOffersAdapter

    private val NO_OFFERS_FOUND = "No offers found :("
    private val SEARCH_QUERY = "Search your offer"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        progress_circular.visibility = View.GONE

        mViewModel = ViewModelProviders.of(this).get(ActiveOfferViewModel::class.java)

        val offers = mViewModel.filteredOffers
        offerAdapter = FilteredOffersAdapter(offers ?: emptyList(),
            OfferAction { offerModel, _ ->
                showOfferDialog(offerModel)
            })

        toggleViews(offers != null && offers.isNotEmpty())

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvFilteredOffers.layoutManager = GridLayoutManager(this, 2)
        } else {
            rvFilteredOffers.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }

        rvFilteredOffers.adapter = offerAdapter

        etSearch.setOnEditorActionListener { _, _, _ ->
            validateAndFind(etSearch.text.toString())
            true

        }
        ivSearch.setOnClickListener {
            validateAndFind(etSearch.text.toString())
        }

    }

    private fun validateAndFind(keyWord: String) {
        if (keyWord.isNotEmpty()) {
            findOffers(keyWord)
        } else {
            etSearch.hint = "Please Type Something"
        }
    }

    private fun findOffers(keyWord: String) {
        startLoadingAnim()

        Handler().post {
            val list = mViewModel.findOffersByKeyWord(keyWord, System.currentTimeMillis())
            offerAdapter.updateOffers(list ?: emptyList(), keyWord)
            stopLoadingAnim(list != null && list.isNotEmpty())
        }

    }

    private fun startLoadingAnim() {
        progress_circular.animate()
        progress_circular.visibility = View.VISIBLE
        rvFilteredOffers.visibility = View.GONE
        ivNoOffersFound.visibility = View.GONE
        tvNoOffersFound.visibility = View.GONE
    }

    private fun stopLoadingAnim(res: Boolean) {
        progress_circular.visibility = View.GONE
        toggleViews(res, NO_OFFERS_FOUND)
    }


    private fun toggleViews(isNotListEmpty: Boolean, text: String = SEARCH_QUERY) {
        if (isNotListEmpty) {
            tvNoOffersFound.visibility = View.GONE
            ivNoOffersFound.visibility = View.GONE
            rvFilteredOffers.visibility = View.VISIBLE

        } else {
            tvNoOffersFound.visibility = View.VISIBLE
            ivNoOffersFound.visibility = View.VISIBLE
            rvFilteredOffers.visibility = View.GONE

            tvNoOffersFound.text = text
        }
    }

    private fun showOfferDialog(offerModel: OfferModel) {
        val builder = AlertDialog.Builder(this)
            .setTitle("${offerModel.vendor} : ${offerModel.offerCode}")
            .setMessage("Offer: ${offerModel.offer} \n\n ${offerModel.message}")
            .setPositiveButton("Copy") { _, _ -> copyToClipboard(offerModel.offerCode, this) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        if (dialog.window != null)
            dialog.window!!.setBackgroundDrawableResource(R.drawable.offer_card_bg_solid)

        dialog.show()
    }
}
