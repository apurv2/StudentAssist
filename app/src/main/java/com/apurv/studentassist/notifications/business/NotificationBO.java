package com.apurv.studentassist.notifications.business;

import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.notifications.interfaces.NotificationBI;

/**
 * Created by akamalapuri on 10/28/2015.
 */
public class NotificationBO {

    NotificationBI notificationBI;

    public NotificationBO(final NotificationBI notificationBI, String url) {

        this.notificationBI = notificationBI;

        StudentAssistBO studentAssistBO = new StudentAssistBO();

        studentAssistBO.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {
                notificationBI.onResponse(jsonResponse);


            }
        });

    }

    public NotificationBO(final LoadingDialog dialog, String url) {
        StudentAssistBO studentAssistBO = new StudentAssistBO();

        studentAssistBO.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                dialog.lodingDialogInterface.onResponse(jsonResponse);
                dialog.dismiss();


            }
        });

    }


}
