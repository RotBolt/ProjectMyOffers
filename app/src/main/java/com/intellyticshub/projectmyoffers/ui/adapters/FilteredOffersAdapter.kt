package com.intellyticshub.projectmyoffers.ui.adapters

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intellyticshub.projectmyoffers.R
import com.intellyticshub.projectmyoffers.data.entity.OfferModel
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction

class FilteredOffersAdapter(private var offers: List<OfferModel>, private val offerAction: OfferAction) :
    RecyclerView.Adapter<FilteredOffersAdapter.OfferHolder>() {

    private var highLightKey = ""


    fun updateOffers(newOffers: List<OfferModel>, newKey: String) {
        offers = newOffers
        highLightKey = newKey
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferHolder {
        val li = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return OfferHolder(li.inflate(R.layout.layout_filtered_offer, parent, false))
    }

    override fun getItemCount() = offers.size

    override fun onBindViewHolder(holder: OfferHolder, position: Int) {
        holder.bind(offers[position])
    }

    inner class OfferHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFilteredCode = itemView.findViewById<TextView>(R.id.tvFilteredCode)
        private val tvFilteredSMS = itemView.findViewById<TextView>(R.id.tvFilteredSMS)
        private val tvFilteredVendor = itemView.findViewById<TextView>(R.id.tvFilteredVendor)

        fun bind(offer: OfferModel) {

            fun SpannableString.highLight(start: Int, end: Int = highLightKey.length) {
                if (start != -1) {
                    setSpan(
                        BackgroundColorSpan(Color.rgb(199, 232, 90)),
                        start,
                        start + end,
                        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
            }

            val spanCode = SpannableString(offer.offerCode).apply {
                highLight(offer.offerCode.toLowerCase().indexOf(highLightKey.toLowerCase()))
            }

            val spanVendor = SpannableString(offer.vendor).apply {
                highLight(offer.vendor.toLowerCase().indexOf(highLightKey.toLowerCase()))
            }

            val index = offer.message.toLowerCase().indexOf(highLightKey.toLowerCase())
            var highLight= offer.message
            if (index != -1) {
                highLight = offer.message.substring(index)
                if (index != 0) {
                    highLight = "...$highLight"
                }
            }
            val cutOutMessage = SpannableString(highLight).apply{
                highLight(if(index!=0) 3 else 0)
            }

            tvFilteredCode.text = spanCode
            tvFilteredSMS.text = cutOutMessage
            tvFilteredVendor.text = spanVendor

            itemView.setOnClickListener {
                offerAction.showOfferActions(offer, adapterPosition)
            }

        }
    }
}