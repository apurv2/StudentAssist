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
    private int universityId;
    private List<String> addPhotoIds = new ArrayList<String>();
    private String univAcronym;
    private String universityPhotoUrl;
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
        universityId = in.readInt();
        addPhotoIds = in.createStringArrayList();
        univAcronym = in.readString();
        universityPhotoUrl = in.readString();
        userVisitedSw = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return "AccommodationAdd{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", apartmentName='" + apartmentName + '\'' +
                ", vacancies='" + vacancies + '\'' +
                ", gender='" + gender + '\'' +
                ", noOfRooms='" + noOfRooms + '\'' +
                ", cost='" + cost + '\'' +
                ", userId='" + userId + '\'' +
                ", addId='" + addId + '\'' +
                ", notes='" + notes + '\'' +
                ", universityName='" + universityName + '\'' +
                ", universityId=" + universityId +
                ", addPhotoIds=" + addPhotoIds +
                ", univAcronym='" + univAcronym + '\'' +
                ", universityPhotoUrl='" + universityPhotoUrl + '\'' +
                ", userVisitedSw=" + userVisitedSw +
                '}';
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

    public static Comparator<AccommodationAdd> getComparatorByUnivId() {

        Comparator<AccommodationAdd> comp = new Comparator<AccommodationAdd>() {
            @Override
            public int compare(AccommodationAdd o1, AccommodationAdd o2) {

                Integer i1 = o1.universityId;
                Integer i2 = o2.universityId;

                return i1.compareTo(i2);
            }
        };

        return comp;

    }

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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setVacancies(String vacancies) {
        this.vacancies = vacancies;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAddId(String addId) {
        this.addId = addId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoUrl() {
        return universityPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.universityPhotoUrl = photoUrl;
    }

    public boolean isUserVisitedSw() {
        return userVisitedSw;
    }


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

    public void setUserVisitedSw(boolean userVisitedSw) {
        this.userVisitedSw = userVisitedSw;
    }

    public boolean getUserVisitedSw() {
        return userVisitedSw;
    }

    public String getNotes() {
        return notes;
    }

    public String getUnivAcronym() {
        return univAcronym;
    }

    public void setUnivAcronym(String univAcronym) {
        this.univAcronym = univAcronym;
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

    public void setNoOfRooms(String noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(emailId);
        dest.writeString(phoneNumber);
        dest.writeString(apartmentName);
        dest.writeString(vacancies);
        dest.writeString(gender);
        dest.writeString(noOfRooms);
        dest.writeString(cost);
        dest.writeString(userId);
        dest.writeString(addId);
        dest.writeString(notes);
        dest.writeString(universityName);
        dest.writeInt(universityId);
        dest.writeStringList(addPhotoIds);
        dest.writeString(univAcronym);
        dest.writeString(universityPhotoUrl);
        dest.writeByte((byte) (userVisitedSw ? 1 : 0));
    }
}
