package com.apurv.studentassist.accommodation.Interfaces;

import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapter;

/**
 * Created by akamalapuri on 8/16/2017.
 */

public interface SearchAccommodationLoadMoreInterface {

    void onLoadMore(int position, AccommodationAddsAdapter adapter, long univId);
}
