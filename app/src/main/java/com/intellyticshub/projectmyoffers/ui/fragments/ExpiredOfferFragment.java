package com.intellyticshub.projectmyoffers.ui.fragments;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.intellyticshub.projectmyoffers.R;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;
import com.intellyticshub.projectmyoffers.data.viewModels.ExpiredOfferViewModel;
import com.intellyticshub.projectmyoffers.ui.adapters.OfferAdapter;
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction;

import java.util.ArrayList;

import static com.intellyticshub.projectmyoffers.utils.OfferUtilsKt.copyToClipboard;

public class ExpiredOfferFragment extends Fragment {

    private ExpiredOfferViewModel mViewModel;

    private RecyclerView rvExpiredOffers;
    private TextView tvNoExpired;
    private ImageView ivNoExpired;

    private String adapterAction = OfferAdapter.UPDATE_ACTION;
    private int adapterItemPosition =-1;

    public static ExpiredOfferFragment newInstance() {
        return new ExpiredOfferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.expired_offer_fragment, container, false);
        rvExpiredOffers = fragView.findViewById(R.id.rvExpiredOffers);
        tvNoExpired = fragView.findViewById(R.id.tvNoExpired);
        ivNoExpired=fragView.findViewById(R.id.ivNoExpired);
        return fragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ExpiredOfferViewModel.class);

        OfferAction offerAction = this::showOfferDialog;

        final OfferAdapter expiredOfferAdapter = new OfferAdapter(
                new ArrayList<>(),
                offerAction,
                true
        );

        mViewModel.getExpiredOffers().observe(this, offerModels -> {
            if (offerModels != null) {
                toggleViews(offerModels.isEmpty());
                expiredOfferAdapter.updateList(offerModels,adapterAction, adapterItemPosition);
                adapterAction=OfferAdapter.UPDATE_ACTION;
                adapterItemPosition=-1;
            } else {
                toggleViews(true);
            }
        });


        rvExpiredOffers.setAdapter(expiredOfferAdapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvExpiredOffers.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            rvExpiredOffers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
    }

    private void toggleViews(boolean isListEmpty) {
        if (isListEmpty) {
            rvExpiredOffers.setVisibility(View.GONE);
            tvNoExpired.setVisibility(View.VISIBLE);
            ivNoExpired.setVisibility(View.VISIBLE);
        } else {
            rvExpiredOffers.setVisibility(View.VISIBLE);
            tvNoExpired.setVisibility(View.GONE);
            ivNoExpired.setVisibility(View.GONE);
        }
    }

    private void showOfferDialog(final OfferModel offerModel,int position) {
        String dialogMessage = "Your Code: " + offerModel.getOfferCode();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage(dialogMessage)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Delete", (dialog, which) ->{
                    adapterAction=OfferAdapter.REMOVE_ACTION;
                    adapterItemPosition =position;
                    mViewModel.deleteOffers(offerModel);
                })
                .setPositiveButton("Copy", (dialog, which) -> copyToClipboard(offerModel.getOfferCode(),getContext()));

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.offer_card_bg_solid);

        dialog.show();

    }

}
