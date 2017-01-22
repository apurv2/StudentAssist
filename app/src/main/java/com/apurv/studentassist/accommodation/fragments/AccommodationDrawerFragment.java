package com.apurv.studentassist.accommodation.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
import com.apurv.studentassist.accommodation.activities.UserPostsActivity;
import com.apurv.studentassist.accommodation.adapters.NavigationDrawerAdapter;
import com.apurv.studentassist.accommodation.classes.NavigationDrawerData;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.util.CircleRectView;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class AccommodationDrawerFragment extends Fragment {


    View mView;
    DrawerLayout mdrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    private View containerView;
    private boolean mFromSavedInstanceState;

    private RecyclerView mRecyclerVIew;
    private NavigationDrawerAdapter mNavagationDrawerAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        pref = getActivity().getApplicationContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        editor = pref.edit();

        mRecyclerVIew = (RecyclerView) mView.findViewById(R.id.drawerList);
        mFromSavedInstanceState = savedInstanceState != null ? true : false;

        mNavagationDrawerAdapter = new NavigationDrawerAdapter(getActivity(), getFromServer());

        mRecyclerVIew.setAdapter(mNavagationDrawerAdapter);
        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mRecyclerVIew.addOnItemTouchListener(new RecyclerListener(getActivity().getApplicationContext(), mRecyclerVIew, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                // User posts fragment
                if (position == 0) {

                    Intent details = new Intent(getActivity().getApplicationContext(), UserPostsActivity.class);
                    details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(details);
                } else if (position == 3) {

                    Intent details = new Intent(getActivity().getApplicationContext(), NotificationSettingsActivity.class);
                    details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(details);

                }

            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));


        new getProfilePicture().execute();

        return mView;
    }


    private class getProfilePicture extends AsyncTask<Void, Void, Bitmap> {

        User user = null;

        @Override
        protected Bitmap doInBackground(Void... params) {

            Bitmap bitmap = null;

            try {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
                byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
                user = (User) ObjectSerializer.deserialize(userInformationBytes);

                URL imageURL = new URL("https://graph.facebook.com/" + user.getUserId() + "/picture?type=large");
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());


            } catch (Exception e) {
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            try {


                CircleRectView circularImageView = (CircleRectView) mView.findViewById(R.id.userPhoto);
                circularImageView.setImageBitmap(bitmap);

                TextView name = (TextView) mView.findViewById(R.id.Title);
                name.setText(user.getFirstName() + " " + user.getLastName());

                TextView email = (TextView) mView.findViewById(R.id.subTitle);
                email.setText("Hope you are doing good!!");
            } catch (NullPointerException e) {
                ErrorReporting.logReport(e);
                Toast.makeText(getContext(), "something has gone wrong,Please Reinstall the app", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public List<NavigationDrawerData> getFromServer() {

        List<NavigationDrawerData> datas = new ArrayList<>();
        int[] icons = {R.drawable.ic_user, R.drawable.ic_clock, R.drawable.ic_action_send, R.drawable.ic_settings1};
        String[] titles = {
                "User Posts", "Recently Viewed", "Post Accommodation", "Notification Settings"};


        for (int i = 0; i < icons.length && i < titles.length; i++) {
            NavigationDrawerData data = new NavigationDrawerData();

            data.setText(titles[i]);
            data.setIconId(icons[i]);

            datas.add(data);

        }
        return datas;

    }


    public void setUp(int fragmentId, final DrawerLayout drawerLayout, Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentId);

        mdrawerLayout = drawerLayout;

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose) {


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                editor.putString(SAConstants.DRAWER_LEARNT, SAConstants.LEARNT);
                editor.commit();


                getActivity().invalidateOptionsMenu();
            }
        };

        mdrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mdrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();


                String drawerLearnt = pref.getString(SAConstants.DRAWER_LEARNT, "");

                if ((!drawerLearnt.equals(SAConstants.LEARNT)) && !mFromSavedInstanceState) {

                    drawerLayout.openDrawer(containerView);


                }

            }
        });


    }


    class RecyclerListener implements RecyclerView.OnItemTouchListener {

        GestureDetector gestureDetector;
        ClickListener clickListener;

        RecyclerListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    return true;


                }

                @Override
                public void onLongPress(MotionEvent e) {

                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));

                    }


                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


            if (child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)) {

                clickListener.onClick(child, recyclerView.getChildPosition(child));


            }


            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }

    public static interface ClickListener {

        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


}
