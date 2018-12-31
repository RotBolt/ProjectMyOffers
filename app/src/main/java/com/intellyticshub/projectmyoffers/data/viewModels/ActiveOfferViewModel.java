package com.intellyticshub.projectmyoffers.data.viewModels;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.intellyticshub.projectmyoffers.data.Repository;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.List;

public class ActiveOfferViewModel extends AndroidViewModel {
    private LiveData<List<OfferModel>> activeOffers;
    private Repository repository;

    public ActiveOfferViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        activeOffers = repository.getActiveOffers();
    }

    public LiveData<List<OfferModel>> getActiveOffers() {
        return activeOffers;
    }

    public void insertOffers(OfferModel ...offerModels){
        repository.insertOffers(offerModels);
    }

    public void deleteOffers(OfferModel ...offerModels){
        repository.deleteOffers(offerModels);
    }

    public void updateOffers(OfferModel ...offerModels){
        repository.updateOffers(offerModels);
    }

    public OfferModel getOfferModel(String offerCode){
        return repository.getOfferModel(offerCode);
    }

}
