package com.apurv.studentassist.accommodation.classes;

/**
 * Created by akamalapuri on 7/31/2016.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class NotificationSettings implements Parcelable {

    private String userId;

    private List<String> apartmentName = new ArrayList<String>();

    private String gender;

    private List<String> apartmentType = new ArrayList<String>();


    private String gcmId;

    private String deviceId;

    protected NotificationSettings(Parcel in) {
        userId = in.readString();
        apartmentName = in.createStringArrayList();
        gender = in.readString();
        apartmentType = in.createStringArrayList();
        gcmId = in.readString();
        deviceId = in.readString();
    }

    public static final Creator<NotificationSettings> CREATOR = new Creator<NotificationSettings>() {
        @Override
        public NotificationSettings createFromParcel(Parcel in) {
            return new NotificationSettings(in);
        }

        @Override
        public NotificationSettings[] newArray(int size) {
            return new NotificationSettings[size];
        }
    };


    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public NotificationSettings() {
    }


    public List<String> getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(List<String> apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getApartmentType() {
        return apartmentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeStringList(apartmentName);
        dest.writeString(gender);
        dest.writeStringList(apartmentType);
        dest.writeString(gcmId);
        dest.writeString(deviceId);
    }
}


