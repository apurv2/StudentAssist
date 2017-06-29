package com.apurv.studentassist.accommodation.classes;

/**
 * Created by akamalapuri on 7/31/2016.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class NotificationSettings implements Parcelable {

    private List<String> apartmentName = new ArrayList<String>();

    private String gender;

    int universityId;

    private String gcmId;

    private String instanceId;

    private List<String> apartmentType = new ArrayList<String>();

    private List<RApartmentNamesInUnivs> apartmentNames = new ArrayList<RApartmentNamesInUnivs>();

    public NotificationSettings(List<String> apartmentName, String gender, int universityId,
                                List<String> apartmentType, List<RApartmentNamesInUnivs> apartmentNames) {
        super();
        this.apartmentName = apartmentName;
        this.gender = gender;
        this.universityId = universityId;
        this.apartmentType = apartmentType;
        this.apartmentNames = apartmentNames;
    }

    protected NotificationSettings(Parcel in) {
        apartmentName = in.createStringArrayList();
        gender = in.readString();
        universityId = in.readInt();
        gcmId = in.readString();
        instanceId = in.readString();
        apartmentType = in.createStringArrayList();
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

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public List<RApartmentNamesInUnivs> getApartmentNames() {
        return apartmentNames;
    }

    public void setApartmentNames(List<RApartmentNamesInUnivs> apartmentNames) {
        this.apartmentNames = apartmentNames;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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

    public void setApartmentType(List<String> apartmentType) {
        this.apartmentType = apartmentType;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public NotificationSettings() {
    }

    @Override
    public String toString() {
        return "RNotificationSettings [apartmentName=" + apartmentName + ", gender=" + gender + ", apartmentType="
                + apartmentType + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(apartmentName);
        dest.writeString(gender);
        dest.writeInt(universityId);
        dest.writeString(gcmId);
        dest.writeString(instanceId);
        dest.writeStringList(apartmentType);
    }
}


