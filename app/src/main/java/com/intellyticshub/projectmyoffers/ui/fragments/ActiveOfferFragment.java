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
import com.intellyticshub.projectmyoffers.data.viewModels.ActiveOfferViewModel;
import com.intellyticshub.projectmyoffers.ui.adapters.OfferAdapter;
import com.intellyticshub.projectmyoffers.ui.interfaces.OfferAction;

import java.util.ArrayList;
import java.util.List;

public class ActiveOfferFragment extends Fragment {

    private ActiveOfferViewModel mViewModel;
    private RecyclerView rvActiveOffers;
    private TextView tvNoActive;

    public static ActiveOfferFragment newInstance() {
        return new ActiveOfferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.active_offer_fragment, container, false);
        rvActiveOffers = fragView.findViewById(R.id.rvActiveOffers);
        tvNoActive = fragView.findViewById(R.id.tvNoActive);
        return fragView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ActiveOfferViewModel.class);

        OfferAction offerAction = new OfferAction() {
            @Override
            public void showOfferActions(OfferModel offerModel) {
                showOfferDialog(offerModel);
            }
        };


        final OfferAdapter activeOfferAdapter = new OfferAdapter(
                new ArrayList<OfferModel>(),
                offerAction,
                false
        );

        mViewModel.getActiveOffers().observe(this, new Observer<List<OfferModel>>() {
            @Override
            public void onChanged(List<OfferModel> offerModels) {
                if (offerModels != null) {
                    toggleViews(offerModels.isEmpty());
                    activeOfferAdapter.updateList(offerModels);
                } else {
                    toggleViews(true);
                }
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

    private void showOfferDialog(final OfferModel offerModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(offerModel.getOfferCode())
                .setMessage(offerModel.getMessage())
                .setPositiveButton("Copy", (dialog, which) -> copyToClipboard(offerModel.getOfferCode()))
                .setNeutralButton("Delete", (dialog, which) -> mViewModel.deleteOffers(offerModel))
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
        } else {
            rvActiveOffers.setVisibility(View.VISIBLE);
            tvNoActive.setVisibility(View.GONE);
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
