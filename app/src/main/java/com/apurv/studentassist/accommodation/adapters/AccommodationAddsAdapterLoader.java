package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
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
import com.apurv.studentassist.accommodation.Interfaces.OnLoadMoreListener;
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


public class AccommodationAddsAdapterLoader extends RecyclerView.Adapter {


    private LayoutInflater inflater;
    ImageView mImage;

    List<AccommodationAdd> mAccommodationAdds = Collections.emptyList();

    private ImageLoader mImageLoader;
    private Network network;
    AccommodationAddsRecyclerInterface parentActivity;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;
    private OnLoadMoreListener onLoadMoreListener;

    public AccommodationAddsAdapterLoader(Context context, List<AccommodationAdd> data, AccommodationAddsRecyclerInterface parentActivity, RecyclerView recyclerView) {

        inflater = LayoutInflater.from(context);
        this.mAccommodationAdds = data;
        network = Network.getNetworkInstnace();
        mImageLoader = network.getmImageLoader();
        this.parentActivity = parentActivity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                linearLayoutManager.findFirstVisibleItemPosition();

                if (!loading && totalItemCount == lastVisibleItem + 1 && totalItemCount >= visibleThreshold) {
                    // End has been reached Do something
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore(totalItemCount);
                    }
                    loading = true;

                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mAccommodationAdds.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder mviewHolder;
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.accomodation_view, viewGroup, false);
            mviewHolder = new AccommodationAddsViewHolder(view);
        } else {
            View v = inflater.inflate(R.layout.progressbar_item, viewGroup, false);
            mviewHolder = new ProgressViewHolder(v);
        }
        return mviewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mAccommodationAddRow, int position) {
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

    public void pop() {
        mAccommodationAdds.remove(mAccommodationAdds.size() - 1);
        notifyItemRemoved(mAccommodationAdds.size());
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

        return mAccommodationAdds.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.recyclerViewProgressBar);
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

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }



}
