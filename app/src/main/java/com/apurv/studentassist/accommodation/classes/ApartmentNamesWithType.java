package com.apurv.studentassist.accommodation.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akamalapuri on 7/23/2016.
 */
public class ApartmentNamesWithType implements Parcelable {

    String apartmentName;
    String apartmentType;

    protected ApartmentNamesWithType(Parcel in) {
        apartmentName = in.readString();
        apartmentType = in.readString();
    }

    public static final Creator<ApartmentNamesWithType> CREATOR = new Creator<ApartmentNamesWithType>() {
        @Override
        public ApartmentNamesWithType createFromParcel(Parcel in) {
            return new ApartmentNamesWithType(in);
        }

        @Override
        public ApartmentNamesWithType[] newArray(int size) {
            return new ApartmentNamesWithType[size];
        }
    };

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(String apartmentType) {
        this.apartmentType = apartmentType;
    }

    public ApartmentNamesWithType(String apartmentName, String apartmentType) {
        super();
        this.apartmentName = apartmentName;
        this.apartmentType = apartmentType;
    }

    public ApartmentNamesWithType() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apartmentName);
        dest.writeString(apartmentType);
    }
}
