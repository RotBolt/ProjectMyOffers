package com.intellyticshub.projectmyoffers.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.List;

@Dao
public interface OfferDao {

    @Query("SELECT * FROM Offers ORDER BY expiryTimeInMillis ASC")
    LiveData<List<OfferModel>> getAllOffers();

    @Query("SELECT * FROM Offers  WHERE expiryTimeInMillis >= :currentTimeMillis OR expiryTimeInMillis==-1 ORDER BY expiryTimeInMillis ASC")
    LiveData<List<OfferModel>> getActiveOffers(Long currentTimeMillis);

    @Query("SELECT * FROM Offers  WHERE expiryTimeInMillis < :currentTimeMillis AND expiryTimeInMillis!=-1 ORDER BY expiryTimeInMillis ASC")
    LiveData<List<OfferModel>> getExpiredOffers(Long currentTimeMillis);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOffers(OfferModel... offerModels);

    @Delete
    void deleteOffers(OfferModel... offerModels);

    @Update
    void updateOffers(OfferModel... offerModels);

    @Query("SELECT * FROM Offers WHERE offerCode = :offerCode")
    OfferModel getOfferModel(String offerCode);

}
