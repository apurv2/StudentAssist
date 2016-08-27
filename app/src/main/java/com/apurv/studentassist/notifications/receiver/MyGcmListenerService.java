package com.apurv.studentassist.notifications.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.AccommodationActivity;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.google.android.gms.gcm.GcmListenerService;

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
            userVisitedSw = data.getBoolean(SAConstants.USER_VISITED_SW);


            sendNotification(new AccommodationAdd(firstName, lastName, emailId,
                    phoneNumber, ApartmentName, vacancies, NoOfRooms, lookingFor,
                    cost, userId, addId, notes, userVisitedSw));

        } else {
            L.m(data.getString("message"));

        }


    }

    /**
     * @param add
     */
    private void sendNotification(AccommodationAdd add) {

        // creating intent to launch AdDetails, putting Accommodation Add in a parcel and passing it to intent
        Intent details = new Intent(this, AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);
        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Building the task stack to add Accommodation Activity as the parent activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AccommodationActivity.class);
        stackBuilder.addNextIntent(details);

        // Adding the stack to the pending intent
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(SAConstants.STUDENT_ASSIST)
                .setContentText(SAConstants.NOTIFICATION_TEXT_APARTMENT)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}