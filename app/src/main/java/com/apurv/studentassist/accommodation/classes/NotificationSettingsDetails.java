package com.apurv.studentassist.accommodation.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akamalapuri on 10/20/2015.
 */
public class NotificationSettingsDetails implements Parcelable {

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }


    private String leftSpinner;
    private String rightSpinner;
    private int notificationId;

    protected NotificationSettingsDetails(Parcel in) {
        leftSpinner = in.readString();
        rightSpinner = in.readString();
        notificationId = in.readInt();
    }

    @Override
    public String toString() {
        return "NotificationSettingsDetails{" +
                "leftSpinner='" + leftSpinner + '\'' +
                ", rightSpinner='" + rightSpinner + '\'' +
                ", notificationId=" + notificationId +
                '}';
    }

    public static final Creator<NotificationSettingsDetails> CREATOR = new Creator<NotificationSettingsDetails>() {
        @Override
        public NotificationSettingsDetails createFromParcel(Parcel in) {
            return new NotificationSettingsDetails(in);
        }

        @Override
        public NotificationSettingsDetails[] newArray(int size) {
            return new NotificationSettingsDetails[size];
        }
    };

    public String getleftSpinner() {
        return leftSpinner;
    }

    public NotificationSettingsDetails(String leftSpinner, String rightSpinner, int notificationId) {
        this.rightSpinner = rightSpinner;
        this.leftSpinner = leftSpinner;
        this.notificationId = notificationId;
    }

    public void setleftSpinner(String leftSpinner) {
        this.leftSpinner = leftSpinner;
    }

    public String getrightSpinner() {
        return rightSpinner;
    }

    public void setrightSpinner(String rightSpinner) {
        this.rightSpinner = rightSpinner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(leftSpinner);
        dest.writeString(rightSpinner);
        dest.writeInt(notificationId);
    }


}
