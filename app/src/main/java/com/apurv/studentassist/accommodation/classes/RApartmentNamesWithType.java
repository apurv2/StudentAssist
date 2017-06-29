package com.apurv.studentassist.accommodation.classes;


public class RApartmentNamesWithType {

    String apartmentName;
    String apartmentType;
    String uinversityId;

    public String getUinversityId() {
        return uinversityId;
    }

    public void setUinversityId(String uinversityId) {
        this.uinversityId = uinversityId;
    }

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

    public RApartmentNamesWithType(String apartmentName, String apartmentType) {
        super();
        this.apartmentName = apartmentName;
        this.apartmentType = apartmentType;
    }

    public RApartmentNamesWithType(String apartmentName, String apartmentType, String uinversityId) {
        super();
        this.apartmentName = apartmentName;
        this.apartmentType = apartmentType;
        this.uinversityId = uinversityId;
    }

    public RApartmentNamesWithType(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public RApartmentNamesWithType() {

    }

}
