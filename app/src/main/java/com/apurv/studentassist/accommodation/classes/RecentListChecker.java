package com.apurv.studentassist.accommodation.classes;

/**
 * Created by akamalapuri on 11/23/2015.
 */
public class RecentListChecker {

    String addId;

    public String getAddId() {
        return addId;
    }

    public void setAddId(String addId) {
        this.addId = addId;
    }

    public RecentListChecker(String addId) {
        this.addId = addId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecentListChecker that = (RecentListChecker) o;

        return getAddId().equals(that.getAddId());

    }

    @Override
    public int hashCode() {
        return getAddId().hashCode();
    }
}
