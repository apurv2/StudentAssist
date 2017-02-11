package com.apurv.studentassist.accommodation.activities;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogDismiss;
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
import com.apurv.studentassist.util.CircleToRectTransition;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.id;
import static com.apurv.studentassist.R.id.ad_details_contactId;

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

    @Bind(R.id.adDetails_placeholder1)
    ImageView imageHolder1;


    @Bind(R.id.ad_details_contactId)
    CardView ad_details_contactId;


    @Bind(R.id.photosId)
    CardView photosId;


    @Bind(R.id.notesCardId)
    CardView notesCardId;

    @Bind(R.id.ad_details_cardId)
    CardView ad_details_cardId;


    @Bind(R.id.adDetailsLinearLayout)
    LinearLayout adDetailsLinearLayout;

    @Bind(R.id.adDetails_placeholder2)
    ImageView imageHolder2;

    @Bind(R.id.adDetails_placeholder3)
    ImageView imageHolder3;
    List<ImageView> imageHolders;


    @Bind(R.id.imageLoader1)
    RelativeLayout imageLoader1;

    @Bind(R.id.imageLoader2)
    RelativeLayout imageLoader2;

    @Bind(R.id.imageLoader3)
    RelativeLayout imageLoader3;
    List<RelativeLayout> imageLoaders;


    @Bind(R.id.imageFrameLayout1)
    FrameLayout frameLayout1;

    @Bind(R.id.imageFrameLayout2)
    FrameLayout frameLayout2;

    @Bind(R.id.imageFrameLayout3)
    FrameLayout frameLayout3;
    List<FrameLayout> frameLayouts;


    AccommodationAdd clickedAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        ButterKnife.bind(this);


        setupEnterAnimation();


        imageHolders = new ArrayList<>(Arrays.asList(imageHolder1, imageHolder2, imageHolder3));
        imageLoaders = new ArrayList<>(Arrays.asList(imageLoader1, imageLoader2, imageLoader3));
        frameLayouts = new ArrayList<>(Arrays.asList(frameLayout1, frameLayout2, frameLayout3));


        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.adDetailsToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        clickedAdd = intent.getExtras().getParcelable(SAConstants.ACCOMMODATION_ADD_PARCELABLE);
        profilePic = intent.getExtras().getParcelable(SAConstants.PROFILE_PIC);

        //Toast.makeText(this, "addId=="+clickedAdd.getAddId(), Toast.LENGTH_SHORT).show();

        loadAccommodationPictures(clickedAdd);

        populateAccommodationDetails();
         //updateRecentlyViewed();

        //Navigation Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdDetailsActivity.super.onBackPressed();
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

        setupTransition(!clickedAdd.getAddPhotoIds().isEmpty());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {

        Transition transition = new CircleToRectTransition();
        transition.setDuration(1500);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementExitTransition(new CircleToRectTransition().setDuration(1500));

    }

     @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition(boolean displayPhotos) {

        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

                List<View> views = new ArrayList<View>();
                views.add(ad_details_contactId);
                views.add(ad_details_cardId);
                views.add(notesCardId);

                if (displayPhotos) {
                    views.add(photosId);
                }

                int delayBetweenAnimations = 130;

                int delayCounter = 0;
                for (View cardView : views) {
                    final View mAddDetailCardView = cardView;

                    // We calculate the delay for this Animation, each animation starts 100ms
                    // after the previous one
                    int delay = delayCounter++ * delayBetweenAnimations;

                    mAddDetailCardView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
                            animation.setDuration(300);
                            mAddDetailCardView.startAnimation(animation);
                            Utilities.showView(mAddDetailCardView);
                        }
                    }, delay);
                }


            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });


    }


    private void loadAccommodationPictures(AccommodationAdd clickedAdd) {

        int counter = 0;

        if (clickedAdd.getAddPhotoIds().isEmpty()) {
            Utilities.hideView(photosId);
        } else {

            for (String url : clickedAdd.getAddPhotoIds()) {

                Utilities.showView(imageLoaders.get(counter));
                Utilities.hideView(imageHolders.get(counter));

                downloadImages(url, imageHolders.get(counter), imageLoaders.get(counter));
                counter++;
            }

            for (; counter < 3; counter++) {
                Utilities.changeToInvisibleView(frameLayouts.get(counter));
            }

        }

    }

    private void downloadImages(String url, ImageView mImageView, RelativeLayout loader) {


        ImageLoader mImageLoader = Network.getNetworkInstnace().getmImageLoader();
        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                Bitmap photo = response.getBitmap();
                if (photo != null) {

                    Utilities.showView(mImageView);
                    Utilities.hideView(loader);

                    mImageView.setImageBitmap(photo);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


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

        } else if (clickedAdd.getGender().equals(SAConstants.MALE_FEMALE[2])) {
            gender_NumberOfVacancies.setText(clickedAdd.getVacancies() + " Roommate(s)");

        } else {
            gender_NumberOfVacancies.setText(clickedAdd.getVacancies() + " " + clickedAdd.getGender() + " Roommate(s)");
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


        sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
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


    @Deprecated
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

    @OnClick(R.id.adDetails_placeholder1)
    public void imageClick(View view) {

        Intent intent = new Intent(this, PhotosViewActivity.class);
        intent.putStringArrayListExtra(SAConstants.ACCOMMODATION_ADD_PHOTOS, (ArrayList<String>) clickedAdd.getAddPhotoIds());
        intent.putExtra(SAConstants.IMAGE_TYPE, SAConstants.CLOUDINARY_IMAGES);
        intent.putExtra(SAConstants.POSITION, 0);
        startActivity(intent);
    }


    @OnClick(R.id.adDetails_placeholder2)
    public void imageClick2(View view) {

        Intent intent = new Intent(this, PhotosViewActivity.class);
        intent.putStringArrayListExtra(SAConstants.ACCOMMODATION_ADD_PHOTOS, (ArrayList<String>) clickedAdd.getAddPhotoIds());
        intent.putExtra(SAConstants.IMAGE_TYPE, SAConstants.CLOUDINARY_IMAGES);
        intent.putExtra(SAConstants.POSITION, 1);
        startActivity(intent);
    }

    @OnClick(R.id.adDetails_placeholder3)
    public void imageClick3(View view) {

        Intent intent = new Intent(this, PhotosViewActivity.class);
        intent.putStringArrayListExtra(SAConstants.ACCOMMODATION_ADD_PHOTOS, (ArrayList<String>) clickedAdd.getAddPhotoIds());
        intent.putExtra(SAConstants.IMAGE_TYPE, SAConstants.CLOUDINARY_IMAGES);
        intent.putExtra(SAConstants.POSITION, 2);
        startActivity(intent);
    }


    // callback method from AlertDialogDismiss.java
    // This closes AdDetails activity when the current Add is deleted by its owner
    public void closeActivity() {

        Intent intent = new Intent(this, AccommodationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
