package com.intellyticshub.projectmyoffers.ui.activities

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import com.intellyticshub.projectmyoffers.data.viewModels.ActiveOfferViewModel
import com.intellyticshub.projectmyoffers.ui.adapters.OfferAdapter
import com.intellyticshub.projectmyoffers.utils.copyToClipboard
import kotlinx.android.synthetic.main.activity_offers_in_range.*
import java.util.*

class OffersInRangeActivity : AppCompatActivity() {

    private lateinit var mViewModel:ActiveOfferViewModel
    private lateinit var list:List<OfferModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers_in_range)

        ivNavBack.setOnClickListener {
            finish()
        }

        mViewModel = ViewModelProviders.of(this).get(ActiveOfferViewModel::class.java)

        val range = intent?.getStringExtra("range")?:"none"

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }

        val begin = with(calendar){
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,11)
            timeInMillis
        }

        list = when (range) {
            "today" -> {
                val end = with(calendar){
                    set(Calendar.HOUR_OF_DAY,23)
                    set(Calendar.MINUTE,59)
                    timeInMillis
                }
                tvActivityTitle.text="Offer Expiring Today"
                mViewModel.getOfferExpiringInRange(begin, end)
            }
            "week" -> {
                val end = calendar.timeInMillis + (7*24*60*60*1000)
                tvActivityTitle.text="Offer Expiring in Week"
                mViewModel.getOfferExpiringInRange(begin, end)
            }
            else ->{
                mViewModel.offerExpiringInRange?: emptyList()
            }
        }
        val offerAdapter = OfferAdapter(
            list,
            {offerModel, _ -> showOfferDialog(offerModel) },
            false
        )

        rvOffersInRange.adapter = offerAdapter

        toggleViews(list.isEmpty())

        val orientation = resources.configuration.orientation
        if (orientation==Configuration.ORIENTATION_LANDSCAPE){
            rvOffersInRange.layoutManager = GridLayoutManager(this,2)
        }else{
            rvOffersInRange.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        }

    }


    private fun showOfferDialog(offerModel: OfferModel) {
        val builder = AlertDialog.Builder(this)
            .setTitle(offerModel.offerCode)
            .setMessage("Offer: ${offerModel.offer} \n\n ${offerModel.message}")
            .setPositiveButton("Copy") { _, _ -> copyToClipboard(offerModel.offerCode, this) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        if (dialog.window != null)
            dialog.window!!.setBackgroundDrawableResource(R.drawable.offer_card_bg_solid)

        dialog.show()
    }

    private fun toggleViews(isListEmpty:Boolean){
        if (isListEmpty){
            rvOffersInRange.visibility= View.GONE
            tvNoOffersFound.visibility=View.VISIBLE
            ivNoOffersFound.visibility=View.VISIBLE
        }else{
            rvOffersInRange.visibility= View.VISIBLE
            tvNoOffersFound.visibility=View.GONE
            ivNoOffersFound.visibility=View.GONE
        }
    }
}
