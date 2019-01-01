package com.intellyticshub.projectmyoffers.ui.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.intellyticshub.projectmyoffers.R;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;
import com.intellyticshub.projectmyoffers.data.viewModels.ActiveOfferViewModel;
import com.intellyticshub.projectmyoffers.ui.adapters.OfferAdapter;
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction;

import java.util.ArrayList;

public class ActiveOfferFragment extends Fragment {

    private ActiveOfferViewModel mViewModel;
    private RecyclerView rvActiveOffers;
    private TextView tvNoActive;
    private ImageView ivNoActive;
    private String adapterAction = OfferAdapter.UPDATE_ACTION;
    private int adapterItemPosition =-1;
    public static ActiveOfferFragment newInstance() {
        return new ActiveOfferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.active_offer_fragment, container, false);
        rvActiveOffers = fragView.findViewById(R.id.rvActiveOffers);
        tvNoActive = fragView.findViewById(R.id.tvNoActive);
        ivNoActive=fragView.findViewById(R.id.ivNoActive);
        return fragView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ActiveOfferViewModel.class);

        OfferAction offerAction = this::showOfferDialog;


        final OfferAdapter activeOfferAdapter = new OfferAdapter(
                new ArrayList<>(),
                offerAction,
                false
        );

        mViewModel.getActiveOffers().observe(this, offerModels -> {
            if (offerModels != null) {
                toggleViews(offerModels.isEmpty());
                activeOfferAdapter.updateList(offerModels,adapterAction, adapterItemPosition);
                adapterItemPosition =-1;
                adapterAction=OfferAdapter.UPDATE_ACTION;
            } else {
                toggleViews(true);
            }
        });

        rvActiveOffers.setAdapter(activeOfferAdapter);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvActiveOffers.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            rvActiveOffers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

    }

    private void showOfferDialog(final OfferModel offerModel,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(offerModel.getOfferCode())
                .setMessage(offerModel.getMessage())
                .setPositiveButton("Copy", (dialog, which) -> copyToClipboard(offerModel.getOfferCode()))
                .setNeutralButton("Delete", (dialog, which) -> {
                    adapterAction=OfferAdapter.REMOVE_ACTION;
                    adapterItemPosition =position;
                    mViewModel.deleteOffers(offerModel);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.offer_card_bg_solid);

        dialog.show();

    }


    private void toggleViews(boolean isListEmpty) {
        if (isListEmpty) {
            rvActiveOffers.setVisibility(View.GONE);
            tvNoActive.setVisibility(View.VISIBLE);
            ivNoActive.setVisibility(View.VISIBLE);
        } else {
            rvActiveOffers.setVisibility(View.VISIBLE);
            tvNoActive.setVisibility(View.GONE);
            ivNoActive.setVisibility(View.GONE);
        }
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
