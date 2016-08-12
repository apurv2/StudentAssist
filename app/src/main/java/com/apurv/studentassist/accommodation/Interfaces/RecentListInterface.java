package com.apurv.studentassist.accommodation.Interfaces;

import com.apurv.studentassist.accommodation.classes.RecentListChecker;

import java.util.List;

/**
 * Created by akamalapuri on 11/23/2015.
 */
public interface RecentListInterface {

    public void recentlyVisitedAdvertisements(List<RecentListChecker> recents);

}
