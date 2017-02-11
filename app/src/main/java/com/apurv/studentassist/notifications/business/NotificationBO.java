package com.apurv.studentassist.notifications.business;

import com.android.volley.Request;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.notifications.interfaces.NotificationBI;

/**
 * Created by akamalapuri on 10/28/2015.
 */
public class NotificationBO {

    NotificationBI notificationBI;

    public NotificationBO(final NotificationBI notificationBI, String body, String url) {

        this.notificationBI = notificationBI;

        StudentAssistBO studentAssistBO = new StudentAssistBO();

        studentAssistBO.volleyRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

            }
        }, body, Request.Method.GET);
    }


}
