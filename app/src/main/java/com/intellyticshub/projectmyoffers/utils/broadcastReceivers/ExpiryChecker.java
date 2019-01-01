package com.intellyticshub.projectmyoffers.utils.broadcastReceivers;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.intellyticshub.projectmyoffers.data.Repository;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExpiryChecker extends BroadcastReceiver {

    public static final int MARKING = 1;
    public static final int DELETE = 2;
    public static final String EXPIRE_CHECK = "expire";
    private static final String EXPIRED_OFFERS = "expired_offers";

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(EXPIRE_CHECK, 0);
        if (status == MARKING) {
            Repository repository = Repository.getInstance((Application) context.getApplicationContext());
            List<OfferModel> allOffers = repository.getAllOffers();
            ArrayList<OfferModel> marked = new ArrayList<>();
            StringBuilder codes = new StringBuilder();
            if (allOffers != null) {
                Long currTimeMillis = System.currentTimeMillis();
                for (OfferModel offer : allOffers) {
                    if (offer.getExpiryTimeInMillis() < currTimeMillis) {
                        codes.append(offer.getOfferCode()).append(";");
                        offer.setDeleteMark(true);
                        marked.add(offer);
                    }
                }
                if (codes.length() > 0) {
                    repository.updateOffers(marked.toArray(new OfferModel[marked.size()]));
                    Long duration = 15 * 24 * 60 * 60 * 1000L;
                    Long targetTime = currTimeMillis + duration;
                    scheduleAutoDelete(context, marked, targetTime);
                }
            }
        } else if (status == DELETE) {
            Repository repository = Repository.getInstance((Application) context.getApplicationContext());
            ArrayList<OfferModel> expiredOffers = intent.getParcelableArrayListExtra(EXPIRED_OFFERS);
            repository.deleteOffers(expiredOffers.toArray(new OfferModel[expiredOffers.size()]));
        }
    }


    private void scheduleAutoDelete(Context context, ArrayList<OfferModel> expiredOffers, Long targetTime) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ExpiryChecker.class);
        intent.putExtra(EXPIRE_CHECK, DELETE);
        intent.putParcelableArrayListExtra(EXPIRED_OFFERS, expiredOffers);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                new Random().nextInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent);
        }

    }
}
