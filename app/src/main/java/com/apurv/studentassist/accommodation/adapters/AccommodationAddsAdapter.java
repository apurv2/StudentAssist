package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationAddsRecyclerInterface;
import com.apurv.studentassist.accommodation.Interfaces.SearchAccommodationLoadMoreInterface;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.Network;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akamalapuri on 12/28/2016.
 */


public class AccommodationAddsAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    ImageView mImage;
    boolean loaderEligible = false;
    char loaderConfig;
    List<AccommodationAdd> mAccommodationAdds = Collections.emptyList();


    private ImageLoader mImageLoader;
    private final int FOOTER_VIEW = 1;
    RecyclerView mRecyclerView;
    private int visibleThreshold = 10;
    private Network network;
    AccommodationAddsRecyclerInterface parentActivity;
    private final int VIEW_ITEM = 0;
    private SearchAccommodationLoadMoreInterface onLoadMoreListener;
    private boolean loading = false;
    private boolean pagination = true;


    public AccommodationAddsAdapter(Context context, List<AccommodationAdd> data,
                                    AccommodationAddsRecyclerInterface parentActivity,
                                    SearchAccommodationLoadMoreInterface onLoadMoreListener,
                                    RecyclerView recyclerView,
                                    char loaderConfig) {

        this.inflater = LayoutInflater.from(context);
        this.mAccommodationAdds = data;
        this.network = Network.getNetworkInstnace();
        this.mImageLoader = network.getmImageLoader();
        this.parentActivity = parentActivity;
        this.onLoadMoreListener = onLoadMoreListener;
        this.mRecyclerView = recyclerView;
        this.loaderConfig = loaderConfig;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder mviewHolder;
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.accomodation_view, viewGroup, false);
            mviewHolder = new AccommodationAddsViewHolder(view);
        } else {
            View v = inflater.inflate(R.layout.load_more, viewGroup, false);
            mviewHolder = new LoadMore(v);
        }
        return mviewHolder;

    }

    @Override
    public int getItemViewType(int position) {
        return position == mAccommodationAdds.size() ? FOOTER_VIEW : VIEW_ITEM;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mAccommodationAddRow, int position) {


        if (mAccommodationAdds.size() % 10 == 0) {
            loaderEligible = true;
        }
        if (mAccommodationAddRow instanceof AccommodationAddsViewHolder) {

            AccommodationAdd mAccommodationAdd = mAccommodationAdds.get(position);
            ((AccommodationAddsViewHolder) mAccommodationAddRow).apartmentName.setText(mAccommodationAdd.getApartmentName());
            ((AccommodationAddsViewHolder) mAccommodationAddRow).noOfRooms.setText(mAccommodationAdd.getNoOfRooms());
            ((AccommodationAddsViewHolder) mAccommodationAddRow).costOfLiving.setText("$" + mAccommodationAdd.getCost());

            if (!mAccommodationAdd.getUserVisitedSw() && ((AccommodationAddsViewHolder) mAccommodationAddRow).userVisited != null) {
                Utilities.hideView(((AccommodationAddsViewHolder) mAccommodationAddRow).userVisited);
            } else {
                Utilities.showView(((AccommodationAddsViewHolder) mAccommodationAddRow).userVisited);
            }

            loadImages(((AccommodationAddsViewHolder) mAccommodationAddRow), mAccommodationAdd.getUserId(), position);
        } else if (loaderEligible) {

            LoadMore loadMore = (LoadMore) mAccommodationAddRow;
            TextView tv = loadMore.loadMoreText;
            ProgressBar progressBar = loadMore.progressBar;

            switch (this.loaderConfig) {
                case SAConstants.LOADER_INDICATOR_S:
                    Utilities.hideView(tv);
                    Utilities.showView(progressBar);
                    break;
                case SAConstants.LOADER_INDICATOR_T:
                    Utilities.showView(tv);
                    Utilities.hideView(progressBar);
                    break;
                case SAConstants.LOADER_INDICATOR_N:
                    Utilities.hideView(tv);
                    Utilities.hideView(progressBar);
                    break;
            }
        }
    }


    private void loadImages(final AccommodationAddsViewHolder holder, String id, final int position) {

        mImageLoader.get(UrlGenerator.getProfilePictureURL(id), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                Bitmap photo = response.getBitmap();
                if (photo != null) {
                    holder.circularImageView.setImageBitmap(photo);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.circularImageView.setTransitionName(SAConstants.THUMBNAIL);
                        holder.circularImageView.setTag(SAConstants.THUMBNAIL);

                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public void add(AccommodationAdd advertisement) {
        mAccommodationAdds.add(advertisement);

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                notifyItemInserted(mAccommodationAdds.size());
            }
        };

        handler.post(r);
    }


    public void changeLoaderStatus(char loaderIndicator) {
        this.loaderConfig = loaderIndicator;
        notifyDataSetChanged();
    }


    public void addAll(List<AccommodationAdd> advertisements) {

        for (int i = 0; i < advertisements.size(); i++) {
            mAccommodationAdds.add(advertisements.get(i));
            notifyItemInserted(mAccommodationAdds.size());
        }
    }

    public void clear() {

        for (int i = mAccommodationAdds.size() - 1; i >= 0; i--) {
            mAccommodationAdds.remove(mAccommodationAdds.get(i));
            notifyItemRemoved(mAccommodationAdds.size());
        }
    }

    @Override
    public int getItemCount() {
        if (mAccommodationAdds == null || mAccommodationAdds.isEmpty()) {
            return 0;
        }
        return mAccommodationAdds.size() + 1;
    }

    public class LoadMore extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView loadMoreText;
        ProgressBar progressBar;

        public LoadMore(View itemView) {
            super(itemView);
            loadMoreText = (TextView) itemView.findViewById(R.id.loaderText);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadingSpinner);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v != null && mAccommodationAdds != null && !mAccommodationAdds.isEmpty()) {
                onLoadMoreListener.onLoadMore(mAccommodationAdds.size(),
                        AccommodationAddsAdapter.this,
                        mAccommodationAdds.get(0).getUniversityId());
            }
        }
    }

    class AccommodationAddsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView apartmentName;
        TextView noOfRooms;
        TextView costOfLiving;
        CircleImageView circularImageView;
        ImageView userVisited;


        public AccommodationAddsViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            apartmentName = (TextView) itemView.findViewById(R.id.large);
            noOfRooms = (TextView) itemView.findViewById(R.id.medium);
            costOfLiving = (TextView) itemView.findViewById(R.id.small);
            circularImageView = (CircleImageView) itemView.findViewById(R.id.userPhoto3);
            userVisited = (ImageView) itemView.findViewById(R.id.userVisited);
        }

        @Override
        public void onClick(View v) {
            if (v != null) {

                Utilities.showView(v.findViewById(R.id.userVisited));
                setUserVisitedAdds(mAccommodationAdds.get(getAdapterPosition()).getAddId());
                parentActivity.onTouch(mAccommodationAdds.get(getAdapterPosition()), v.findViewById(R.id.userPhoto3));
            }
        }
    }

    private void setUserVisitedAdds(String addId) {
        try {

            UrlInterface urlGen = new UrlGenerator();
            String url = urlGen.setUserVisitedAdds();
            StudentAssistBO dbManager = new StudentAssistBO();

            Gson gson = new Gson();
            String body = gson.toJson(new UserVisitedAdds(addId));

            L.m("body==" + body);

            dbManager.volleyRequest(url, new NetworkInterface() {
                @Override
                public void onResponseUpdate(String jsonResponse) {

                    L.m("response from setUserVisitedAdds==" + jsonResponse);

                }
            }, body, Request.Method.PUT);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    private class UserVisitedAdds {
        String addId;

        public UserVisitedAdds(String addId) {
            this.addId = addId;
        }

    }

    public boolean isEmpty() {
        return mAccommodationAdds.isEmpty();
    }

}
