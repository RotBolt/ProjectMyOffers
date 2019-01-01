package com.intellyticshub.projectmyoffers.utils.broadcastReceivers;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.intellyticshub.projectmyoffers.R;
import com.intellyticshub.projectmyoffers.data.Repository;
import com.intellyticshub.projectmyoffers.data.entity.OfferModel;
import com.intellyticshub.projectmyoffers.ui.actvities.MainActivity;
import com.intellyticshub.projectmyoffers.utils.OfferExtractor;

import java.util.Calendar;

public class NewOfferDetector extends BroadcastReceiver {

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] msgs;
                String format = bundle.getString("format");
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    boolean isVersionM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
                    msgs = new SmsMessage[pdus.length];
                    StringBuilder sms = new StringBuilder();
                    for (int i = 0; i < msgs.length; i++) {
                        if (isVersionM) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        sms.append(msgs[i].getMessageBody());
                    }
                    String vendor = msgs[0].getOriginatingAddress();
                    if (vendor != null) {
                        OfferExtractor offerExtractor = new OfferExtractor(sms.toString());
                        String offerCode = offerExtractor.extractOfferCode();
                        String offer = offerExtractor.extractOffer();
                        if (!offer.equals("none") && !offerCode.equals("none")) {

                            Long currTimeMillis = System.currentTimeMillis();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(currTimeMillis);
                            String currYear = String.valueOf(calendar.get(Calendar.YEAR));
                            OfferExtractor.ExpiryDateInfo expiryDateInfo = offerExtractor.extractExpiryDate(currYear);

                            String expiryDate;
                            switch (expiryDateInfo.getExpiryDate()) {
                                case "last day":
                                case "expiring today":
                                    expiryDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                                    break;
                                case "none":
                                    expiryDate = "";
                                    break;
                                default:
                                    expiryDate = expiryDateInfo.getExpiryDate();
                            }

                            Long expiryTimeMillis;
                            if (expiryDateInfo.getExpiryTimeInMillis() == -2L)
                                expiryTimeMillis = currTimeMillis;
                            else
                                expiryTimeMillis = expiryDateInfo.getExpiryTimeInMillis();

                            OfferModel newOffer = new OfferModel(
                                    offerCode,
                                    offer,
                                    vendor,
                                    expiryDate,
                                    sms.toString(),
                                    expiryTimeMillis,
                                    false
                            );
                            Repository repository = Repository.getInstance((Application) context.getApplicationContext());
                            repository.insertOffers(newOffer);
                            if (expiryTimeMillis > currTimeMillis) {
                                sendNotification(context, offerCode, offer);
                            }
                        }
                    }
                }
            }
        }
    }


    private void sendNotification(Context context, String code, String offer) {

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel("channel_id", name, NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
        Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_coupon_outline)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_coupon_outline))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle("New Offer Came")
                .setContentText("Code : " + code + " Offer : " + offer)
                .setContentIntent(pendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("channel_id"); // Channel ID
        }

        builder.setAutoCancel(true);

        if (mNotificationManager != null) {
            mNotificationManager.notify(0, builder.build());
        }
    }

}
