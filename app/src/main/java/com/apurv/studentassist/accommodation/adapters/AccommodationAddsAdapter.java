package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.internet.Network;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.util.CircleRectView;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.facebook.AccessToken;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

/**
 * Created by apurv on 6/23/15.
 */
public class AccommodationAddsAdapter extends RecyclerView.Adapter<AccommodationAddsAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    ImageView mImage;

    List<AccommodationAdd> mAccommodationAdds = Collections.emptyList();

    private ImageLoader mImageLoader;
    private Network network;
    RecyclerTouchInterface parentActivity;

    public AccommodationAddsAdapter(Context context, List<AccommodationAdd> data, RecyclerTouchInterface parentActivity) {

        inflater = LayoutInflater.from(context);
        this.mAccommodationAdds = data;
        network = Network.getNetworkInstnace();
        mImageLoader = network.getmImageLoader();
        this.parentActivity = parentActivity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = inflater.inflate(R.layout.accomodation_view, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AccommodationAddsAdapter.MyViewHolder mAccommodationAddRow, int i) {


        AccommodationAdd mAccommodationAdd = mAccommodationAdds.get(i);


        mAccommodationAddRow.apartmentName.setText(mAccommodationAdd.getApartmentName());
        mAccommodationAddRow.noOfRooms.setText(mAccommodationAdd.getNoOfRooms());
        mAccommodationAddRow.costOfLiving.setText("$" + mAccommodationAdd.getCost());

        if (!mAccommodationAdd.getUserVisitedSw() && mAccommodationAddRow.userVisited != null) {
            Utilities.hideView(mAccommodationAddRow.userVisited);
        }


        loadImages(mAccommodationAddRow, mAccommodationAdd.getUserId(), i);

    }

    private void loadImages(final AccommodationAddsAdapter.MyViewHolder holder, String id, final int position) {


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


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView apartmentName;
        TextView noOfRooms;
        TextView costOfLiving;
        CircleRectView circularImageView;
        ImageView userVisited;


        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            apartmentName = (TextView) itemView.findViewById(R.id.large);
            noOfRooms = (TextView) itemView.findViewById(R.id.medium);
            costOfLiving = (TextView) itemView.findViewById(R.id.small);
            circularImageView = (CircleRectView) itemView.findViewById(R.id.userPhoto3);
            userVisited = (ImageView) itemView.findViewById(R.id.userVisited);


        }

        @Override
        public void onClick(View v) {
            if (v != null) {
                Utilities.showView(v.findViewById(R.id.userVisited));
                setUserVisitedAdds(mAccommodationAdds.get(getAdapterPosition()).getAddId());
                parentActivity.onTouch(getAdapterPosition(), v.findViewById(R.id.userPhoto3));
            }
        }
    }

    private void setUserVisitedAdds(String addId) {
        try {

            UrlInterface urlGen = new UrlGenerator();
            String url = urlGen.setUserVisitedAdds();
            StudentAssistBO dbManager = new StudentAssistBO();

            NotificationSettings settings = new NotificationSettings();

            if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

                String fbToken = AccessToken.getCurrentAccessToken().getToken();

                UserVisitedAdds userVisitedAdds = new UserVisitedAdds(addId, fbToken);
                Gson gson = new Gson();
                String body = gson.toJson(userVisitedAdds);

                L.m("body==" + body);

                dbManager.volleyPostRequest(url, new NetworkInterface() {
                    @Override
                    public void onResponseUpdate(String jsonResponse) {

                        L.m("response from setUserVisitedAdds==" + jsonResponse);

                    }
                }, body);
            }


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    private class UserVisitedAdds {

        String addId;
        String access_token;

        public UserVisitedAdds(String addId, String token) {
            this.addId = addId;
            this.access_token = token;
        }
    }

}
