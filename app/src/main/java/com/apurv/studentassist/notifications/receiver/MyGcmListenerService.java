package com.apurv.studentassist.notifications.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.AccommodationActivity;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.activities.HomeScreenActivity;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;

/**
 * Created by akamalapuri on 10/30/2015.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String notificationType = "";

        notificationType = data.getString(SAConstants.NOTIFICATION_TYPE);

        if (notificationType != null && notificationType.equals(SAConstants.ACCOMMODATION_ADD)) {

            String firstName, lastName, emailId, phoneNumber, ApartmentName, vacancies, NoOfRooms, lookingFor, cost, userId, addId, notes;
            boolean userVisitedSw;

            firstName = data.getString(SAConstants.FIRST_NAME);
            lastName = data.getString(SAConstants.LAST_NAME);
            emailId = data.getString(SAConstants.EMAIL_ID);
            phoneNumber = data.getString(SAConstants.PHONE_NUMBER);
            ApartmentName = data.getString(SAConstants.APARTMENT_NAME);
            vacancies = data.getString(SAConstants.VACANCIES);
            NoOfRooms = data.getString(SAConstants.NO_OF_ROOMS);
            lookingFor = data.getString(SAConstants.GENDER);
            cost = data.getString(SAConstants.COST);
            userId = data.getString(SAConstants.USER_ID);
            addId = data.getString(SAConstants.ADD_ID);
            notes = data.getString(SAConstants.NOTES);
            userVisitedSw = true;


            sendNotification(new AccommodationAdd(firstName, lastName, emailId,
                    phoneNumber, ApartmentName, vacancies, NoOfRooms, lookingFor,
                    cost, userId, addId, notes, userVisitedSw, new ArrayList<String>()));

        } else {
            L.m(data.getString("message"));

        }


    }

    private Spannable makeBold(String notification) {

        int boldStartPos = notification.indexOf("  ");

        Spannable sb = new SpannableString(notification);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, boldStartPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    /**
     * @param add
     */
    private void sendNotification(AccommodationAdd add) {   // creating intent to launch AdDetails, putting Accommodation Add in a parcel and passing it to intent

        //declaring intents
        Intent resultIntent;
        Intent backIntent;

        //getting shared preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        String storedNotifications = pref.getString(SAConstants.NOTIFICATION_LIST, "");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.InboxStyle notificationStyle = new NotificationCompat.InboxStyle();
        String currentNotification = add.getFirstName() + "  " + add.getApartmentName() + " " + add.getCost() + " $";
        notificationStyle.addLine(makeBold(currentNotification));
        currentNotification += SAConstants.COMMA;
        if (storedNotifications.isEmpty()) {

            resultIntent = new Intent(this, AdDetailsActivity.class);
            backIntent = new Intent(this, AccommodationActivity.class);
            resultIntent.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);

        } else {
            resultIntent = new Intent(this, AccommodationActivity.class);
            backIntent = new Intent(this, HomeScreenActivity.class);

            for (String param : storedNotifications.substring(0, storedNotifications.length() - 1).split(",")) {
                notificationStyle.addLine(makeBold(param));
            }
        }
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent resultPendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{backIntent, resultIntent}, PendingIntent.FLAG_ONE_SHOT);


        storedNotifications += currentNotification;
        editor.putString(SAConstants.NOTIFICATION_LIST, storedNotifications);
        editor.commit();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(SAConstants.STUDENT_ASSIST)
                .setStyle(notificationStyle
                        .setBigContentTitle(SAConstants.STUDENT_ASSIST)
                        .setSummaryText("We have some vacancies for you"))
                .setGroup("apartments group")
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of currentNotification */, notificationBuilder.build());
    }
}