package com.intellyticshub.projectmyoffers.data.viewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.intellyticshub.projectmyoffers.data.Repository;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.List;

public class ExpiredOfferViewModel extends AndroidViewModel {
    private LiveData<List<OfferModel>> expiredOffers;
    private Repository repository;

    public ExpiredOfferViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        expiredOffers = repository.getExpiredOffers();
    }

    public LiveData<List<OfferModel>> getExpiredOffers() {
        return expiredOffers;
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
