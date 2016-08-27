package com.apurv.studentassist.airport.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akamalapuri on 11/15/2015.
 */
public class AirportService implements Parcelable {

    private String serviceName;
    private String description;
    private String link;


    public AirportService(String serviceName, String description, String link) {
        this.serviceName = serviceName;
        this.description = description;
        this.link = link;

    }

    protected AirportService(Parcel in) {
        serviceName = in.readString();
        description = in.readString();
        link = in.readString();
    }

    public static final Creator<AirportService> CREATOR = new Creator<AirportService>() {
        @Override
        public AirportService createFromParcel(Parcel in) {
            return new AirportService(in);
        }

        @Override
        public AirportService[] newArray(int size) {
            return new AirportService[size];
        }
    };

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirportService that = (AirportService) o;

        if (!serviceName.equals(that.serviceName)) return false;
        return description.equals(that.description);

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceName);
        dest.writeString(description);
        dest.writeString(link);
    }


}
