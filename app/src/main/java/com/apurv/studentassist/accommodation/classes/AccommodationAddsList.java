package com.apurv.studentassist.accommodation.classes;

import java.util.List;

/**
 * Created by akamalapuri on 3/20/2017.
 */

public class AccommodationAddsList {

    List addsList1;
    List addsList2;
    List addsList3;
    List addsList4;

    public AccommodationAddsList(List addsList1, List addsList2, List addsList3, List addsList4) {
        this.addsList1 = addsList1;
        this.addsList2 = addsList2;
        this.addsList3 = addsList3;
        this.addsList4 = addsList4;
    }


    public List getAddsList1() {
        return addsList1;
    }

    public void setAddsList1(List addsList1) {
        this.addsList1 = addsList1;
    }

    public List getAddsList2() {
        return addsList2;
    }

    public void setAddsList2(List addsList2) {
        this.addsList2 = addsList2;
    }

    public List getAddsList3() {
        return addsList3;
    }

    public void setAddsList3(List addsList3) {
        this.addsList3 = addsList3;
    }

    public List getAddsList4() {
        return addsList4;
    }

    public void setAddsList4(List addsList4) {
        this.addsList4 = addsList4;
    }
}
