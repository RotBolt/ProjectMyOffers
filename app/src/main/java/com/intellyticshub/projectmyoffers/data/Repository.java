package com.intellyticshub.projectmyoffers.data;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.List;
import java.util.concurrent.*;

public class Repository {

    private OfferDao offerDao;
    private LiveData<List<OfferModel>> allOffers;
    private LiveData<List<OfferModel>> activeOffers;
    private LiveData<List<OfferModel>> expiredOffers;

    private ExecutorService executor;
    private Long currentTimeMillis;
    private static Repository instance;


    public static Repository getInstance(Application application) {
        if (instance != null) return instance;

        synchronized (Repository.class) {
            instance = new Repository(application);
            return instance;
        }
    }

    private Repository(Application application) {

        OfferDatabase offerDatabase = OfferDatabase.getDatabase(application);
        offerDao = offerDatabase.offerDao();
        allOffers = offerDao.getAllOffersLive();
        currentTimeMillis = System.currentTimeMillis();
        activeOffers = offerDao.getActiveOffers(currentTimeMillis);
        expiredOffers = offerDao.getExpiredOffers(currentTimeMillis);
        executor = Executors.newFixedThreadPool(3);
    }

    public void setCurrentTimeMillis(Long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
        activeOffers = offerDao.getActiveOffers(currentTimeMillis);
        expiredOffers = offerDao.getExpiredOffers(currentTimeMillis);
    }


    public LiveData<List<OfferModel>> getAllOffersLive() {
        return allOffers;
    }

    public LiveData<List<OfferModel>> getActiveOffers() {
        return activeOffers;
    }

    public LiveData<List<OfferModel>> getExpiredOffers() {
        return expiredOffers;
    }

    public List<OfferModel> findOffersByKeyWords(String keyWord, Long currentTimeMillis) {
        Callable<List<OfferModel>> filterOfferTask = () -> offerDao.findOffersByKeyWord("%" + keyWord + "%", currentTimeMillis);

        Future<List<OfferModel>> futureOffers = executor.submit(filterOfferTask);

        try {
            return futureOffers.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<OfferModel> getAllOffers() {
        Callable<List<OfferModel>> getOffersTask = () -> offerDao.getAllOffers();

        Future<List<OfferModel>> futureAllOffers = executor.submit(getOffersTask);

        try {
            return futureAllOffers.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<OfferModel> getMarkedDeleteOffers() {
        Callable<List<OfferModel>> getOffersTask = () -> offerDao.getMarkedDeleteOffers();

        Future<List<OfferModel>> futureDeleteOffers = executor.submit(getOffersTask);

        try {
            return futureDeleteOffers.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void insertOffers(final OfferModel... offerModels) {
        Callable<Void> insertTask = () -> {
            offerDao.insertOffers(offerModels);
            return null;
        };

        Future futureInsert = executor.submit(insertTask);
        try {
            futureInsert.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateOffers(final OfferModel... offerModels) {
        Callable<Void> updateTask = () -> {
            offerDao.updateOffers(offerModels);
            return null;
        };

        Future futureUpdate = executor.submit(updateTask);

        try {
            futureUpdate.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteOffers(final OfferModel... offerModels) {
        Callable<Void> deleteTask = () -> {
            offerDao.deleteOffers(offerModels);
            return null;
        };

        Future futureDelete = executor.submit(deleteTask);

        try {
            futureDelete.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public OfferModel getOfferModel(final String offerCode) {
        Callable<OfferModel> offerTask = () -> offerDao.getOfferModel(offerCode);

        Future futureOfferModel = executor.submit(offerTask);

        try {
            return (OfferModel) futureOfferModel.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OfferModel> getOfferExpiringInRange(Long begin, Long end) {
        Callable<List<OfferModel>> task = () -> offerDao.getOffersExpiringInRange(begin, end);
        Future<List<OfferModel>> future = executor.submit(task);

        try {

            return future.get();
        } catch (ExecutionException e) {
            Log.i("PUI","execption");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("PUI","execption");
            return null;
        }
    }

}
