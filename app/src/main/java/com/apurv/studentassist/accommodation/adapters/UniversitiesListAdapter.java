package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.University;
import com.apurv.studentassist.internet.Network;

import java.util.Collections;
import java.util.List;

/**
 * Created by akamalapuri on 3/15/2017.
 */
public class UniversitiesListAdapter extends RecyclerView.Adapter {


    private LayoutInflater inflater;
    ImageView mImage;

    List<University> mUniversities = Collections.emptyList();

    private ImageLoader mImageLoader;
    private Network network;
    public UniversitiesListAdapter(Context context, List<University> data) {

        inflater = LayoutInflater.from(context);
        this.mUniversities = data;
        network = Network.getNetworkInstnace();
        mImageLoader = network.getmImageLoader();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        View view = inflater.inflate(R.layout.single_university_view, viewGroup, false);
        RecyclerView.ViewHolder mviewHolder = new UniversitiesViewHolder(view);

        return mviewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mUniversityRow, int position) {
        if (mUniversityRow instanceof UniversitiesViewHolder) {

            University mUniversity = mUniversities.get(position);


            ((UniversitiesViewHolder) mUniversityRow).universityName.setText(mUniversity.getUniversityName() + " (" + mUniversity.getEstdYear() + ")");
            ((UniversitiesViewHolder) mUniversityRow).UniversityAddress.setText(mUniversity.getLocation());
            ((UniversitiesViewHolder) mUniversityRow).noOfListings.setText(mUniversity.getNoOfListings());
            ((UniversitiesViewHolder) mUniversityRow).noOfUsers.setText(mUniversity.getNoOfUsers());

            loadImages(((UniversitiesViewHolder) mUniversityRow), mUniversity.getUrls().get(0), position);
        }
    }

    private void loadImages(final UniversitiesViewHolder holder, String url, final int position) {


        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                Bitmap photo = response.getBitmap();
                if (photo != null) {
                    holder.universityPhoto.setImageBitmap(photo);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public void add(University university) {
        mUniversities.add(university);
        notifyItemInserted(mUniversities.size());
    }

    public void pop() {
        mUniversities.remove(mUniversities.size() - 1);
        notifyItemRemoved(mUniversities.size());
    }

    public void addAll(List<University> universityList) {

        for (int i = 0; i < universityList.size(); i++) {
            mUniversities.add(universityList.get(i));
            notifyItemInserted(mUniversities.size());
        }
    }

    public void clear() {

        for (int i = mUniversities.size() - 1; i >= 0; i--) {
            mUniversities.remove(mUniversities.get(i));
            notifyItemRemoved(mUniversities.size());
        }


    }

    @Override
    public int getItemCount() {

        return mUniversities.size();
    }


    class UniversitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView universityName;
        TextView UniversityAddress;
        TextView noOfListings;
        TextView noOfUsers;
        ImageView universityPhoto;


        public UniversitiesViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            universityName = (TextView) itemView.findViewById(R.id.universityName);
            UniversityAddress = (TextView) itemView.findViewById(R.id.UniversityAddress);
            noOfListings = (TextView) itemView.findViewById(R.id.noOfListings);
            noOfUsers = (TextView) itemView.findViewById(R.id.noOfUsers);
            universityPhoto = (ImageView) itemView.findViewById(R.id.universityPhoto);
        }

        @Override
        public void onClick(View v) {
            if (v != null) {

            }
        }
    }
}

