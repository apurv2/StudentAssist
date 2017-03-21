package com.apurv.studentassist.accommodation.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AccommodationAdd implements java.io.Serializable, Parcelable {

    private String firstName;
    private String lastName;
    private String emailId;
    private String phoneNumber;
    private String apartmentName;
    private String vacancies;
    private String gender;
    private String noOfRooms;
    private String cost;
    private String userId;
    private String addId;
    private String notes;
    private String universityName;
    private String universityId;
    private List<String> addPhotoIds = new ArrayList<String>();

    private boolean userVisitedSw;

    protected AccommodationAdd(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        emailId = in.readString();
        phoneNumber = in.readString();
        apartmentName = in.readString();
        vacancies = in.readString();
        gender = in.readString();
        noOfRooms = in.readString();
        cost = in.readString();
        userId = in.readString();
        addId = in.readString();
        notes = in.readString();
        universityName = in.readString();
        universityId = in.readString();
        addPhotoIds = in.createStringArrayList();
        userVisitedSw = in.readByte() != 0;
    }


    public static Comparator<AccommodationAdd> getComparatorByUnivId() {
        Comparator<AccommodationAdd> comp = (accommodationAdd, t1) -> accommodationAdd.universityId.compareTo(t1.universityId);
        return comp;
    }


    public static final Creator<AccommodationAdd> CREATOR = new Creator<AccommodationAdd>() {
        @Override
        public AccommodationAdd createFromParcel(Parcel in) {
            return new AccommodationAdd(in);
        }

        @Override
        public AccommodationAdd[] newArray(int size) {
            return new AccommodationAdd[size];
        }
    };

    public List<String> getAddPhotoIds() {
        return addPhotoIds;
    }

    public void setAddPhotoIds(List<String> addPhotoIds) {
        this.addPhotoIds = addPhotoIds;
    }


    public AccommodationAdd(String firstName, String lastName, String emailId, String phoneNumber,
                            String apartmentName, String vacancies, String gender, String noOfRooms,
                            String cost, String userId, String addId, String notes, boolean userVisitedSw, List<String> apartmentPictureId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.apartmentName = apartmentName;
        this.vacancies = vacancies;
        this.gender = gender;
        this.noOfRooms = noOfRooms;
        this.cost = cost;
        this.userId = userId;
        this.addId = addId;
        this.notes = notes;
        this.userVisitedSw = userVisitedSw;
        this.addPhotoIds = apartmentPictureId;

    }

    public AccommodationAdd(String apartmentName, String noOfRooms, String vacancies, String cost,
                            String gender, String notes, List<String> apartmentPictureId) {


        this.apartmentName = apartmentName;
        this.noOfRooms = noOfRooms;
        this.vacancies = vacancies;
        this.cost = cost;
        this.gender = gender;
        this.notes = notes;
        this.addPhotoIds = apartmentPictureId;

    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public void setUserVisitedSw(boolean userVisitedSw) {
        this.userVisitedSw = userVisitedSw;
    }

    public boolean getUserVisitedSw() {
        return userVisitedSw;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccommodationAdd that = (AccommodationAdd) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        return addId.equals(that.addId);

    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + addId.hashCode();
        return result;
    }


    public String getAddId() {
        return addId;
    }


    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public String getVacancies() {
        return vacancies;
    }

    public String getNoOfRooms() {
        return noOfRooms;
    }

    public String getGender() {
        return gender;
    }

    public String getCost() {
        return cost;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(emailId);
        parcel.writeString(phoneNumber);
        parcel.writeString(apartmentName);
        parcel.writeString(vacancies);
        parcel.writeString(gender);
        parcel.writeString(noOfRooms);
        parcel.writeString(cost);
        parcel.writeString(userId);
        parcel.writeString(addId);
        parcel.writeString(notes);
        parcel.writeString(universityName);
        parcel.writeString(universityId);
        parcel.writeStringList(addPhotoIds);
        parcel.writeByte((byte) (userVisitedSw ? 1 : 0));
    }
}
