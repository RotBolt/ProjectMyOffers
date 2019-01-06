package com.intellyticshub.projectmyoffers.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.List;

@Dao
public interface OfferDao {

    @Query("SELECT * FROM Offers ORDER BY expiryTimeInMillis ASC")
    LiveData<List<OfferModel>> getAllOffersLive();

    @Query("SELECT * FROM Offers ORDER BY expiryTimeInMillis ASC")
    List<OfferModel> getAllOffers();

    @Query("SELECT * FROM Offers  WHERE expiryTimeInMillis >= :currentTimeMillis ORDER BY expiryTimeInMillis ASC")
    LiveData<List<OfferModel>> getActiveOffers(Long currentTimeMillis);

    @Query("SELECT * FROM Offers  WHERE expiryTimeInMillis < :currentTimeMillis ORDER BY expiryTimeInMillis DESC")
    LiveData<List<OfferModel>> getExpiredOffers(Long currentTimeMillis);

    @Query("SELECT * FROM Offers WHERE deleteMark=1")
    List<OfferModel> getMarkedDeleteOffers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOffers(OfferModel... offerModels);

    @Delete
    void deleteOffers(OfferModel... offerModels);

    @Update
    void updateOffers(OfferModel... offerModels);

    @Query("SELECT * FROM Offers WHERE offerCode = :offerCode")
    OfferModel getOfferModel(String offerCode);

    @Query("SELECT * FROM Offers WHERE expiryTimeInMillis >= :currTimeMillis AND (offerCode LIKE :keyWord OR message LIKE :keyWord OR vendor LIKE :keyWord)")
    List<OfferModel> findOffersByKeyWord(String keyWord, Long currTimeMillis);

    @Query("SELECT * FROM Offers WHERE expiryTimeInMillis>=:begin AND expiryTimeInMillis<=:end")
    List<OfferModel> getOffersExpiringInRange(Long begin, Long end);

}
