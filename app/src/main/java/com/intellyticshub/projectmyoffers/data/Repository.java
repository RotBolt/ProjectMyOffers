package com.intellyticshub.projectmyoffers.data;

import android.app.Application;
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
        allOffers = offerDao.getAllOffers();

        Long currentTimeMillis = System.currentTimeMillis();
        activeOffers = offerDao.getActiveOffers(currentTimeMillis);
        expiredOffers = offerDao.getExpiredOffers(currentTimeMillis);

        executor = Executors.newFixedThreadPool(3);
    }

    public LiveData<List<OfferModel>> getAllOffers() {
        return allOffers;
    }

    public LiveData<List<OfferModel>> getActiveOffers() {
        return activeOffers;
    }

    public LiveData<List<OfferModel>> getExpiredOffers() {
        return expiredOffers;
    }

    public void insertOffers(final OfferModel... offerModels) {
        Callable insertTask = new Callable<Void>() {
            @Override
            public Void call() {
                offerDao.insertOffers(offerModels);
                return null;
            }
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
        Callable updateTask = new Callable<Void>() {
            @Override
            public Void call() {
                offerDao.updateOffers(offerModels);
                return null;
            }
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
        Callable deleteTask = new Callable<Void>() {
            @Override
            public Void call() {
                offerDao.deleteOffers(offerModels);
                return null;
            }
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
        Callable offerTask = new Callable<OfferModel>() {
            @Override
            public OfferModel call() {
                return offerDao.getOfferModel(offerCode);
            }
        };

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


}
