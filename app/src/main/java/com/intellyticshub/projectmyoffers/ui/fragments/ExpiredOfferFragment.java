package com.intellyticshub.projectmyoffers.ui.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import java.util.List;

public class ExpiredOfferFragment extends Fragment {

    private ExpiredOfferViewModel mViewModel;

    private RecyclerView rvExpiredOffers;
    private TextView tvExpiryInfo;
    private TextView tvNoExpired;

    public static ExpiredOfferFragment newInstance() {
        return new ExpiredOfferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.expired_offer_fragment, container, false);
        rvExpiredOffers = fragView.findViewById(R.id.rvExpiredOffers);
        tvExpiryInfo = fragView.findViewById(R.id.tvExpiryInfo);
        tvNoExpired = fragView.findViewById(R.id.tvNoExpired);
        return fragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ExpiredOfferViewModel.class);

        OfferAction offerAction = new OfferAction() {
            @Override
            public void showOfferActions(OfferModel offerModel) {
                showOfferDialog(offerModel);
            }
        };

        final OfferAdapter expiredOfferAdapter = new OfferAdapter(
                new ArrayList<OfferModel>(),
                offerAction,
                true
        );

        mViewModel.getExpiredOffers().observe(this, new Observer<List<OfferModel>>() {
            @Override
            public void onChanged(List<OfferModel> offerModels) {
                if (offerModels != null) {
                    toggleViews(offerModels.isEmpty());
                    expiredOfferAdapter.updateList(offerModels);
                } else {
                    toggleViews(true);
                }
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
            tvExpiryInfo.setVisibility(View.GONE);
            tvNoExpired.setVisibility(View.VISIBLE);
        } else {
            rvExpiredOffers.setVisibility(View.VISIBLE);
            tvNoExpired.setVisibility(View.GONE);
            tvExpiryInfo.setVisibility(View.VISIBLE);
        }
    }

    private void showOfferDialog(final OfferModel offerModel) {

        String dialogMessage = "Your Code: " + offerModel.getOfferCode();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage(dialogMessage)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.deleteOffers(offerModel);
                    }
                })
                .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyToClipboard(offerModel.getOfferCode());
                    }
                });

    }

    private void copyToClipboard(String offerCode) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("OfferModel Code", offerCode);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), offerCode + " copied", Toast.LENGTH_SHORT).show();
        }
    }

}
