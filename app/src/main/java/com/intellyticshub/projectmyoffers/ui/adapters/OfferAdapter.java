package com.intellyticshub.projectmyoffers.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.intellyticshub.projectmyoffers.R;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {

    private List<OfferModel> offers;
    private OfferAction offerAction;
    private boolean isExpired;

    public OfferAdapter(List<OfferModel> offers, OfferAction offerAction, boolean isExpired) {
        this.offers = offers;
        this.offerAction = offerAction;
        this.isExpired = isExpired;
    }

    public void updateList(List<OfferModel> newOffers) {
        this.offers = newOffers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert li != null;
        View offerView = li.inflate(R.layout.layout_offer_item, parent, false);
        return new OfferHolder(offerView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferHolder holder, int position) {
        if (offers != null) {
            holder.bind(offers.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return offers != null ? offers.size() : 0;
    }

    class OfferHolder extends RecyclerView.ViewHolder {

        TextView tvOfferCode;
        TextView tvOffer;
        TextView tvVendor;
        TextView tvExpiryDate;


        OfferHolder(@NonNull View itemView) {
            super(itemView);
            tvOfferCode = itemView.findViewById(R.id.tvOfferCode);
            tvOffer = itemView.findViewById(R.id.tvOffer);
            tvVendor = itemView.findViewById(R.id.tvVendor);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
        }

        void bind(final OfferModel thisModel) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    offerAction.showOfferActions(thisModel);
                }
            });
            String offerCodeLabel = "Your Code: " + thisModel.getOfferCode();
            tvOfferCode.setText(offerCodeLabel);

            String offerLabel = "Your Offer: " + thisModel.getOffer();
            tvOffer.setText(offerLabel);

            tvVendor.setText(thisModel.getVendor());
            tvExpiryDate.setText(thisModel.getExpiryDate());

            if (isExpired){
                tvExpiryDate.setTextColor(Color.RED);
            }

        }
    }
}
