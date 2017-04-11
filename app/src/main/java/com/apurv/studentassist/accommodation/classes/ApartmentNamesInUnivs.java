package com.apurv.studentassist.accommodation.classes;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ApartmentNamesInUnivs implements Parcelable {


    String universityName;
    int universityId;
    List<ApartmentNamesWithType> apartmentNames;

    public ApartmentNamesInUnivs() {
    }

    protected ApartmentNamesInUnivs(Parcel in) {
        universityName = in.readString();
        universityId = in.readInt();
        apartmentNames = in.createTypedArrayList(ApartmentNamesWithType.CREATOR);
    }

    public static final Creator<ApartmentNamesInUnivs> CREATOR = new Creator<ApartmentNamesInUnivs>() {
        @Override
        public ApartmentNamesInUnivs createFromParcel(Parcel in) {
            return new ApartmentNamesInUnivs(in);
        }

        @Override
        public ApartmentNamesInUnivs[] newArray(int size) {
            return new ApartmentNamesInUnivs[size];
        }
    };

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public List<ApartmentNamesWithType> getApartmentNames() {
        return apartmentNames;
    }

    public void setApartmentNames(List<ApartmentNamesWithType> apartmentNames) {
        this.apartmentNames = apartmentNames;
    }

    public ApartmentNamesInUnivs(String universityName, int universityId,
                                 List<ApartmentNamesWithType> apartmentNames) {
        super();
        this.universityName = universityName;
        this.universityId = universityId;
        this.apartmentNames = apartmentNames;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(universityName);
        dest.writeInt(universityId);
        dest.writeTypedList(apartmentNames);
    }
}
