package com.apurv.studentassist.accommodation.classes;

import java.util.List;

/**
 * Created by akamalapuri on 3/15/2017.
 */


public class University {

    private int universityId;

    private String universityName;

    private String description;

    private List<String> urls;

    private int noOfUsers;

    private String location;

    private int estdYear;

    private int noOfListings;

    public University(int universityId, String universityName, String description, List<String> urls, int noOfUsers,
                      String location, int estdYear, int noOfListings) {
        this.universityId = universityId;
        this.universityName = universityName;
        this.description = description;
        this.urls = urls;
        this.noOfUsers = noOfUsers;
        this.location = location;
        this.estdYear = estdYear;
        this.noOfListings = noOfListings;
    }

    public int getNoOfListings() {
        return noOfListings;
    }

    public void setNoOfListings(int noOfListings) {
        this.noOfListings = noOfListings;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEstdYear() {
        return estdYear;
    }

    public void setEstdYear(int estdYear) {
        this.estdYear = estdYear;
    }

}