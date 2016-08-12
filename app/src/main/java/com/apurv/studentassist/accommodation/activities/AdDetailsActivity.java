package com.apurv.studentassist.accommodation.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogDismiss;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.DeleteAccommodationAdd;
import com.apurv.studentassist.accommodation.Dialogs.ImageViewDialog;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Dialogs.TakeDownPost;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.Network;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;

import java.util.ArrayList;
import java.util.List;

//Using Serialization because parcel cannot be stored into shared Preferences

public class AdDetailsActivity extends AppCompatActivity implements LodingDialogInterface {


    Bitmap imageBitmap;
    Bundle bundle;
    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Bitmap profilePic;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;


    AccommodationAdd clickedAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);


        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.adDetailsToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        clickedAdd = intent.getExtras().getParcelable(SAConstants.ACCOMMODATION_ADD_PARCELABLE);
        profilePic = intent.getExtras().getParcelable(SAConstants.PROFILE_PIC);


        populateAccommodationDetails();
        // updateRecentlyViewed();

        //Navigation Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ImageView profilePictureImage = (ImageView) findViewById(R.id.adDetailsImageView);
        profilePictureImage.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                DialogFragment enlargedImageFragment = new ImageViewDialog();


                if (profilePic == null) {


                    enlargedImageFragment.setArguments(getIntent()
                            .putExtra(SAConstants.PROFILE_PIC, imageBitmap).getExtras());


                } else {
                    enlargedImageFragment.setArguments(getIntent().getExtras());


                }


                enlargedImageFragment.show(getSupportFragmentManager(), "");
            }
        });


        Button takeDownPost = (Button) findViewById(R.id.takeDownButton);
        takeDownPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString(SAConstants.ADD_ID, clickedAdd.getAddId());

                DialogFragment takeDownDialog = new TakeDownPost();
                takeDownDialog.setArguments(bundle);
                takeDownDialog.show(getSupportFragmentManager(), "");

            }
        });
    }

    @Deprecated
    private void updateRecentlyViewed() {

        sharedPreferences = getSharedPreferences(SAConstants.sharedPreferenceName, 0);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString(SAConstants.RECENTS, "").equals("")) {

            List<AccommodationAdd> recentsList = new ArrayList<AccommodationAdd>();
            recentsList.add(clickedAdd);

            editor.putString(SAConstants.RECENTS, Base64.encodeToString(ObjectSerializer.serialize(recentsList), Base64.DEFAULT));

        } else {

            byte[] recentListBytes = Base64.decode(sharedPreferences.getString(SAConstants.RECENTS, ""), Base64.DEFAULT);
            List<AccommodationAdd> recentsList = (List<AccommodationAdd>) ObjectSerializer.deserialize(recentListBytes);


            if (recentsList.contains(clickedAdd)) {

                recentsList.remove(clickedAdd);
                recentsList.add(0, clickedAdd);


            } else {
                recentsList.add(0, clickedAdd);

                if (recentsList.size() > 10) {
                    recentsList.remove(10);
                }


            }


            editor.putString(SAConstants.RECENTS, Base64.encodeToString(ObjectSerializer.serialize(recentsList), Base64.DEFAULT));
        }
        editor.commit();


    }


    private void populateAccommodationDetails() {


        ImageView mImg;
        mImg = (ImageView) findViewById(R.id.adDetailsImageView);
        displayContactDetails();

        fetchProfilePic(clickedAdd.getUserId(), mImg);

        TextView apartmentName = (TextView) findViewById(R.id.apartmentName);
        TextView gender_NumberOfVacancies = (TextView) findViewById(R.id.gender_NumberOfVacancies);
        TextView numberOfRooms = (TextView) findViewById(R.id.numberOfRooms);
        TextView cost = (TextView) findViewById(R.id.cost);
        TextView notes = (TextView) findViewById(R.id.notes);

        notes.setText(clickedAdd.getNotes());
        apartmentName.setText(clickedAdd.getApartmentName());
        numberOfRooms.setText(clickedAdd.getNoOfRooms());
        cost.setText(clickedAdd.getCost() + " per month");
        mCollapsingToolbarLayout.setTitle(clickedAdd.getFirstName() + " " + clickedAdd.getLastName());


        if (clickedAdd.getVacancies().equals(SAConstants.LEASE_TRANSFER)) {
            gender_NumberOfVacancies.setText(clickedAdd.getVacancies());

        } else if (clickedAdd.getLookingFor().equals(SAConstants.MALE_FEMALE[2])) {
            gender_NumberOfVacancies.setText(clickedAdd.getVacancies() + " Roommate(s)");

        } else {
            gender_NumberOfVacancies.setText(clickedAdd.getVacancies() + " " + clickedAdd.getLookingFor() + " Roommate(s)");
        }

    }

    private void fetchProfilePic(String userId, final ImageView image) {

        ImageLoader mImageLoader;
        Network network;
        network = Network.getNetworkInstnace();
        mImageLoader = network.getmImageLoader();

        L.m("userId==" + userId);

        mImageLoader.get(UrlGenerator.getProfilePictureURL(userId), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                Bitmap photo = response.getBitmap();
                if (photo != null) {
                    imageBitmap = photo;
                    image.setImageBitmap(photo);
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });


    }

    private void displayContactDetails() {


        sharedPreferences = getSharedPreferences(SAConstants.sharedPreferenceName, 0);
        byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
        User user = (User) ObjectSerializer.deserialize(userInformationBytes);


        if (user.getUserId().equals(clickedAdd.getUserId())) {

            Utilities.showView(findViewById(R.id.deleteButton));
            Utilities.hideView(findViewById(R.id.contactButtons));
        } else {

            Utilities.hideView(findViewById(R.id.deleteButton));
            Utilities.showView(findViewById(R.id.contactButtons));
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void callPoster() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + clickedAdd.getPhoneNumber()));
        startActivity(callIntent);


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + clickedAdd.getPhoneNumber()));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        Bundle b = new Bundle();
                        b.putString(SAConstants.ALERT_TEXT, "");
                        AlertDialogL AlertDialogL = new AlertDialogL();
                        AlertDialogL.show(getSupportFragmentManager(), "");
                        return;
                    }
                    startActivity(callIntent);


                } else {

                    Bundle b = new Bundle();
                    b.putString(SAConstants.ALERT_TEXT, "");
                    AlertDialogL AlertDialogL = new AlertDialogL();
                    AlertDialogL.show(getSupportFragmentManager(), "");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void emailPoster(View view) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{clickedAdd.getPhoneNumber()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vacancy in your apartment");
        intent.putExtra(
                Intent.EXTRA_TEXT,
                "Dear "
                        + clickedAdd.getFirstName()
                        + " "
                        + clickedAdd.getLastName()
                        + "\n I saw your post in StudentAssist App for an accommodation vacancy and I am interested in being your roommate.");
        startActivity(Intent.createChooser(intent, ""));

    }

    public void deletePost(View view) {


        DeleteAccommodationAdd deleteAddDialog = new DeleteAccommodationAdd();
        deleteAddDialog.show(getSupportFragmentManager(), "");


    }

    public void deletePostCallback() {
        UrlInterface urlGen = new UrlGenerator();
        String addId = clickedAdd.getAddId();

        final AdDetailsActivity adDetailsActivity = this;

        try {

            final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.DELETING_POST, getSupportFragmentManager());
            String url = urlGen.getDeleteAccommodationPostUrl(addId);
            new AccommodationBO(url, loadingDialog);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    public void facebookMessage(View view) {


        Intent facebookIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/" + clickedAdd.getUserId()));

        startActivity(facebookIntent);

    }


    // Delete Accommodation Response
    @Override
    public void onResponse(String response) {

        AlertDialogDismiss dialog = new AlertDialogDismiss();
        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, SAConstants.SUCCESSFULLY_DELETED);
        dialog.setArguments(b);
        dialog.show(getSupportFragmentManager(), "");


    }


    // callback method from AlertDialogDismiss.java
    // This closes AdDetails activity when the current Add is deleted by its owner
    public void closeActivity() {

        Intent intent = new Intent(this, AccommodationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


        finish();
    }
}
