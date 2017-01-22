package com.apurv.studentassist.accommodation.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AccommodationPosted;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Dialogs.NewApartmentDialog;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.PostAccommodationBI;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.GUIUtils;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.apurv.studentassist.util.interfaces.OnRevealAnimationListener;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostAccomodationActivity extends AppCompatActivity implements
        OnItemSelectedListener, LodingDialogInterface {

    @Bind(R.id.activity_contact_rl_container)
    RelativeLayout mRlContainer;

    @Bind(R.id.activity_contact_fab)
    FloatingActionButton mFab;

    @Bind(R.id.parent)
    LinearLayout mLlContainer;

    @Bind(R.id.placeholder1)
    ImageView imageHolder1;

    @Bind(R.id.placeholder2)
    ImageView imageHolder2;

    @Bind(R.id.placeholder3)
    ImageView imageHolder3;
    List<ImageView> imageHolders;


    UrlInterface urlGen = new UrlGenerator();
    Spinner apartmentTypeSpinner, noOfRoomsSpinner, noOfVacanciesSpinner, occupantSexSpinner, apartmentNameSpinner;
    List<String> errorQueue = new ArrayList<String>();
    SharedPreferences sharedPreferences;
    private View pageView;
    Bundle bundle;
    Boolean reEntryFlag = false;
    ArrayList<String> mApartmentNames;
    String aptTypeSpinnerVal = "";
    Cloudinary cloudinary;
    List<File> mImagesList = new ArrayList<File>();
    List<String> filePaths = new ArrayList<>();
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private final int MY_PERMISSIONS_REQUEST_CAMERA_EXTERNAL_STORAGE = 1;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_accomodation);
        pageView = findViewById(R.id.parent);
        ButterKnife.bind(this);

        imageHolders = new ArrayList<>(Arrays.asList(imageHolder1, imageHolder2, imageHolder3));
        cloudinary = new Cloudinary("cloudinary://647816789382186:5R3U1Oc9zwvnPOfI-TtlIeI0u_E@duf1ntj7z");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        } else {
            initViews();
        }

        if (savedInstanceState != null) {
            mLlContainer.setVisibility(View.VISIBLE);

            bundle = savedInstanceState;
            reEntryFlag = true;
            ArrayList<String> selectedFilePaths;
            selectedFilePaths = bundle.getStringArrayList(SAConstants.IMAGE_FILE_PATHS);

            for (String selectedFilePath : selectedFilePaths) {

                File file = new File(selectedFilePath);

                filePaths.add(selectedFilePath);
                mImagesList.add(file);


                Bitmap myBitmap = Utilities.decodeSampledBitmapFromResource(file, 100, 100);

                WeakReference<Bitmap> imageViewReference;
                ImageView mImage = imageHolders.get(mImagesList.size() - 1);

                imageViewReference = new WeakReference<Bitmap>(myBitmap);
                mImage.setImageBitmap(imageViewReference.get());
            }
        }

        if (mImagesList.isEmpty()) {
            Utilities.hideView(findViewById(R.id.selectedPhotosView_post));
        }

        // setup toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.POST_ACCOMMODATION);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Activity activity = (Activity) this;

        //Navigation Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GUIUtils.animateRevealHide(activity, mRlContainer, R.color.colorAccent, mFab.getWidth() / 2,
                        new OnRevealAnimationListener() {
                            @Override
                            public void onRevealHide() {
                                closeActivity(false);
                            }

                            @Override
                            public void onRevealShow() {

                            }
                        });
            }
        });


        //set spinners
        setSpinners();

        final Button postVacancy = (Button) pageView.findViewById(R.id.postVacancy);
        postVacancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateAll()) {

                    if (filePaths.isEmpty()) {
                        postVacancy(Collections.emptyList(),
                                Utilities.showLoadingDialog(SAConstants.POSTING_ACCOMMODAION, getSupportFragmentManager()));

                    } else {
                        startImagesUpload(filePaths);
                    }

                }
            }
        });

    }

    @OnClick(R.id.placeholder1)
    public void placeholder1(View view) {

        openPhotosViewActivity(0);
    }

    @OnClick(R.id.placeholder2)
    public void placeholder2(View view) {

        openPhotosViewActivity(1);
    }

    @OnClick(R.id.placeholder3)
    public void placeholder3(View view) {
        openPhotosViewActivity(2);

    }


    @OnClick(R.id.camera)
    public void camera(View view) {


        if (checkCameraHardware(getApplicationContext())) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {


                if (handleDontDisplayPermissionsPopup(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA_EXTERNAL_STORAGE);

                    //Storing requested permission into Shared Preferences
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Manifest.permission.CAMERA, Manifest.permission.CAMERA);

                    //Storing requested permission into Shared Preferences
                    editor.putString(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);


                    editor.commit();
                }

            } else {

                dispatchTakePictureIntent();
            }

        } else {

            Utilities.showALertDialog("Camera is not available on this device", getSupportFragmentManager());
        }

    }

    /**
     * Checks if the user checked the checkbox to disallow permission popup. if so show the application permissions page.
     *
     * @param permissionNames
     * @return true if the user did not check disallow checkbox else returns false.
     */
    public boolean handleDontDisplayPermissionsPopup(String[] permissionNames) {

        sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        String permission;
        for (String permissionName : permissionNames) {
            permission = sharedPreferences.getString(permissionName, "");

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissionName) && permission.equals(permissionName) &&
                    ActivityCompat.checkSelfPermission(this, permissionName) != PackageManager.PERMISSION_GRANTED) {

                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getApplicationContext().startActivity(i);

                return false;
            }
        }

        return true;

    }


    @OnClick(R.id.gallery)
    public void gallery(View view) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            if (handleDontDisplayPermissionsPopup(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {


                // If the permission is not already granted, open dialog box to ask for permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


                //Storing requested permission into Shared Preferences
                SharedPreferences pref = getApplicationContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                editor.commit();
            }


        } else {

            if (mImagesList.size() < 3) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else {
                Utilities.showALertDialog("You can load maximum of 3 images", getSupportFragmentManager());
            }
        }
    }

    /**
     * Callback method after the user interacts with the Permissions popup
     *
     * @param requestCode  contains the request code for the permission
     * @param permissions  array that contains all permissions
     * @param grantResults array containing code for granted and not granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (mImagesList.size() < 3) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    } else {
                        Utilities.showALertDialog("You can load maximum of 3 images", getSupportFragmentManager());
                    }

                    break;
                }
            case MY_PERMISSIONS_REQUEST_CAMERA_EXTERNAL_STORAGE:

                boolean denied = false;
                for (int permission_result : grantResults) {
                    if (permission_result == PackageManager.PERMISSION_DENIED) {
                        denied = true;
                    }
                }
                if (!denied) {
                    dispatchTakePictureIntent();

                }


        }


    }

    private void dispatchTakePictureIntent() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2);
    }


    /**
     * opens a new activity with ViewPager to view photos
     *
     * @param position
     */
    private void openPhotosViewActivity(int position) {

        try {

            List selectedFilePaths = new ArrayList<String>();

            for (String filePath : filePaths) {
                selectedFilePaths.add(filePath);
            }


            Intent intent = new Intent(this, PhotosViewActivity.class);
            intent.putStringArrayListExtra(SAConstants.ACCOMMODATION_ADD_PHOTOS, (ArrayList<String>) selectedFilePaths);
            intent.putExtra(SAConstants.IMAGE_TYPE, SAConstants.LOCAL_IMAGES);
            intent.putExtra(SAConstants.POSITION, position);
            intent.putExtra(SAConstants.CALLING_ACTIVITY, SAConstants.POST_ACCOMMODATION_ACTIVITY);
            startActivityForResult(intent, 3);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                String[] column = {MediaStore.Images.Media.DATA};


                Cursor cursor;
                if (android.os.Build.VERSION.SDK_INT < 23) {
                    cursor = getContentResolver().query(uri, null, null, null, null);

                } else {
                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String id = wholeID.split(":")[1];
                    String sel = MediaStore.Images.Media._ID + "=?";

                    cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);

                }


                int columnIndex = cursor.getColumnIndex(column[0]);

                File file = null;
                if (cursor.moveToFirst()) {
                    filePaths.add(cursor.getString(columnIndex));
                    file = new File(cursor.getString(columnIndex));
                }
                cursor.close();


                if (file != null && file.exists()) {

                    if (mImagesList.size() < 3 && !mImagesList.contains(file)) {

                        Utilities.showView(findViewById(R.id.selectedPhotosView_post));
                        mImagesList.add(file);

                        Bitmap myBitmap = Utilities.decodeSampledBitmapFromResource(file, 100, 100);
                        WeakReference<Bitmap> imageViewReference;
                        ImageView mImage = imageHolders.get(mImagesList.size() - 1);
                        imageViewReference = new WeakReference<Bitmap>(myBitmap);
                        mImage.setImageBitmap(imageViewReference.get());

                    }

                }


            }
        }
        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {

                if (mImagesList.size() < 3) {

                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getApplicationContext().getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, null, null, null);
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);


                    File file = null;
                    if (cursor.moveToLast()) {
                        filePaths.add(cursor.getString(columnIndex));
                        file = new File(cursor.getString(columnIndex));
                    }
                    cursor.close();

                    if (file != null && file.exists()) {

                        if (mImagesList.size() < 3 && !mImagesList.contains(file)) {

                            Utilities.showView(findViewById(R.id.selectedPhotosView_post));
                            mImagesList.add(file);
                            Bitmap myBitmap = Utilities.decodeSampledBitmapFromResource(file, 100, 100);

                            WeakReference<Bitmap> imageViewReference;
                            ImageView mImage = imageHolders.get(mImagesList.size() - 1);

                            imageViewReference = new WeakReference<Bitmap>(myBitmap);
                            mImage.setImageBitmap(imageViewReference.get());
                        }
                    }
                } else {
                    Utilities.showALertDialog("You can load maximum of 3 images", getSupportFragmentManager());
                }

            }
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                List<Integer> deletedPhotos = data.getIntegerArrayListExtra(SAConstants.DELETED_PHOTOS);
                for (int deletedPhoto : deletedPhotos) {
                    for (int j = deletedPhoto; j < mImagesList.size() - 1; j++) {
                        imageHolders.get(j).setImageDrawable(imageHolders.get(j + 1).getDrawable());
                    }
                    imageHolders.get(mImagesList.size() - 1).setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery));
                    filePaths.remove(deletedPhoto);
                    mImagesList.remove(deletedPhoto);
                }
                L.m(filePaths.size() + "");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void startImagesUpload(final List<String> filePaths) {

        final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.UPLOADING_IMAGES, getSupportFragmentManager());


        AsyncTask<Void, Void, List> task = new AsyncTask<Void, Void, List>() {
            protected List doInBackground(Void... paths) {
                Map cloudinaryResult;
                List<String> cloudinaryUrls = new ArrayList<>();

                try {

                    for (String filePath : filePaths) {
                        File file = new File(filePath);
                        Bitmap uploadBitmap = Utilities.decodeSampledBitmapFromResource(file, 500, 500);

                        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                getResources().getString(R.string.app_name));
                        // Create the storage directory if it does not exist
                        if (!imageStorageDir.exists()) {
                            imageStorageDir.mkdirs();
                        }

                        File resizedFile = new File(imageStorageDir, "resize.png");

                        OutputStream fOut = null;
                        try {
                            fOut = new BufferedOutputStream(new FileOutputStream(resizedFile));
                            uploadBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            uploadBitmap.recycle();

                        } catch (Exception e) {

                            ErrorReporting.logReport(e);
                        }


                        cloudinaryResult = cloudinary.uploader().
                                upload(resizedFile, ObjectUtils.emptyMap());

                        resizedFile.delete();

                        if (cloudinaryResult.containsKey("url")) {
                            cloudinaryUrls.add(String.valueOf(cloudinaryResult.get("url")));
                        }
                    }
                } catch (RuntimeException e) {
                    ErrorReporting.logReport(e);
                } catch (IOException e) {
                    ErrorReporting.logReport(e);
                }
                return cloudinaryUrls;
            }

            @Override
            protected void onPostExecute(List cloudinaryUrlsList) {

                if (cloudinaryUrlsList.isEmpty()) {
                    loadingDialog.dismiss();
                    Utilities.showALertDialog(SAConstants.VOLLEY_ERROR, getSupportFragmentManager());
                } else {
                    postVacancy(cloudinaryUrlsList, loadingDialog);
                    loadingDialog.changeText(SAConstants.POSTING_ACCOMMODAION);
                }

            }
        };
        task.execute();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(mRlContainer);
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

    private void animateRevealShow(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;

        Drawable d = getResources().getDrawable(R.drawable.wall_2);
        GUIUtils.animateRevealShow(this, mRlContainer, mFab.getWidth() / 2, d,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {


        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(300);
            mLlContainer.startAnimation(animation);
            mLlContainer.setVisibility(View.VISIBLE);

        });


    }

    @Override
    public void onBackPressed() {
        GUIUtils.animateRevealHide(this, mRlContainer, R.color.colorAccent, mFab.getWidth() / 2,
                new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        closeActivity(false);
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimation() {
        Fade fade = new Fade();
        getWindow().setReturnTransition(fade);
        fade.setDuration(300);
    }


    public void postVacancy(List cloudinaryUrls, final LoadingDialog loadingDialog) {

        try {

            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String noOfVacancies = noOfVacanciesSpinner.getSelectedItem().toString();
            String lookingFor = occupantSexSpinner.getSelectedItem().toString();
            String apartmentName = apartmentNameSpinner.getSelectedItem().toString();
            String noOfRooms = noOfRoomsSpinner.getSelectedItem().toString();

            EditText Field = (EditText) pageView
                    .findViewById(R.id.costOfLivingPerMonth);
            String cost = Field.getText().toString();

            EditText mNotes = (EditText) pageView.findViewById(R.id.notesText);
            String notes = mNotes.getText().toString();

            AccommodationAdd mAccommodationAdd = new AccommodationAdd(apartmentName, noOfRooms, noOfVacancies,
                    cost, lookingFor, notes, cloudinaryUrls);

            Gson gson = new Gson();
            String postAccommodationJson = gson.toJson(mAccommodationAdd);

            StudentAssistBO manager = new StudentAssistBO();
            manager.volleyPostRequestWithLoadingDialog(urlGen.getPostAccUrl(), loadingDialog, postAccommodationJson);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void setSpinners() {

        // data from local
        apartmentTypeSpinner = (Spinner) pageView.findViewById(R.id.aptTypeSpinner);
        apartmentTypeSpinner.setAdapter(createArrayAdapter(R.array.apartment_type));


        // data from local
        noOfRoomsSpinner = (Spinner) pageView.findViewById(R.id.noOfRoomsSpinner);
        noOfRoomsSpinner.setAdapter(createArrayAdapter(R.array.no_of_rooms));

        // data from local
        noOfVacanciesSpinner = (Spinner) pageView.findViewById(R.id.noOfVacanciesSpinner);
        noOfVacanciesSpinner.setAdapter(createArrayAdapter(R.array.no_of_vacancies));

        // data from local
        occupantSexSpinner = (Spinner) pageView.findViewById(R.id.occcupantSexSpinner);
        occupantSexSpinner.setAdapter(createArrayAdapter(R.array.occcupant_gender));

        // data from server
        apartmentNameSpinner = (Spinner) pageView.findViewById(R.id.aptNameSpinner);
        apartmentNameSpinner.setAdapter(createArrayAdapter(new ArrayList<String>()));

        apartmentTypeSpinner.setOnItemSelectedListener(this);
        apartmentNameSpinner.setOnItemSelectedListener(this);


    }

    public ArrayAdapter<String> createArrayAdapter(List<String> List) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, List);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public ArrayAdapter<CharSequence> createArrayAdapter(int id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }


    /**
     * Populate apartmentNames spinner and frame accommodationAdds Recycler View URL
     *
     * @param parent
     * @param spinnerView
     * @param position
     * @param spinnerId
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View spinnerView, int position,
                               long spinnerId) {

        try {


            if (parent.getId() == R.id.aptTypeSpinner) {

                if (!aptTypeSpinnerVal.equals(apartmentTypeSpinner.getSelectedItem().toString())) {

                    aptTypeSpinnerVal = apartmentTypeSpinner.getSelectedItem().toString();

                    L.m("came to apt type spinner");

                    final ArrayAdapter<String> aptNamesAdapter = (ArrayAdapter<String>) apartmentNameSpinner.getAdapter();
                    aptNamesAdapter.clear();


                    if (reEntryFlag) {
                        mApartmentNames = bundle.getStringArrayList(SAConstants.APARTMENT_NAME);
                        aptNamesAdapter.addAll(mApartmentNames);
                        aptNamesAdapter.notifyDataSetChanged();

                        L.m("spinner position from bundle=" + bundle.getInt(SAConstants.APARTMENT_NAME_POSITION));


                        apartmentNameSpinner.setSelection(bundle.getInt(SAConstants.APARTMENT_NAME_POSITION));
                        reEntryFlag = false;
                    } else {

                        String url = urlGen.getApartmentNamesUrl(apartmentTypeSpinner.getSelectedItem().toString());

                        new AccommodationBO(url, new AccommodationBI() {
                            @Override
                            public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                            }

                            @Override
                            public void onApartmentNamesReady(ArrayList<String> apartmentNames) {
                                mApartmentNames = apartmentNames;
                                aptNamesAdapter.addAll(apartmentNames);
                                aptNamesAdapter.add(SAConstants.OTHER);
                                aptNamesAdapter.notifyDataSetChanged();
                            }


                        }, SAConstants.APARTMENT_NAMES);

                    }
                }
            } else if (parent.getId() == R.id.aptNameSpinner) {


                if (SAConstants.OTHER.equals(apartmentNameSpinner.getItemAtPosition(position).toString())) {


                    DialogFragment apartmentName = new NewApartmentDialog();
                    apartmentName.show(getSupportFragmentManager(), "");

                }


            }

        } catch (
                Exception e
                )

        {
            ErrorReporting.logReport(e);
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public void aptNameCallback(String apartmentName) {


        String apartmentType = apartmentTypeSpinner.getSelectedItem().toString();

        addNewApartment(apartmentType, apartmentName);

    }

    public void apartmentNameCancelCallback() {

        apartmentNameSpinner.setSelection(0);
    }


    private boolean validateAll() {

        errorQueue.clear();

        if (validateSpinners() && validateCostOfLiving() && validateNotes()) {
            return true;
        }


        String errorContent = "";

        for (String error : errorQueue) {
            errorContent = errorContent + "*" + error + "\n\n";
        }

        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, errorContent);

        AlertDialogL errorDialog = new AlertDialogL();
        errorDialog.setArguments(b);
        errorDialog.show(getSupportFragmentManager(), "");

        return false;


    }


    public void addNewApartment(final String apartmentType, final String apartmentName) {
        try {


            String url = urlGen.getAddNewAptUrl(apartmentName, apartmentType);


            new AccommodationBO(url, new PostAccommodationBI() {
                @Override
                public void onResponse(String response) {

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) apartmentNameSpinner
                            .getAdapter();
                    adapter.remove(SAConstants.OTHER);
                    adapter.add(apartmentName);
                    adapter.add(SAConstants.OTHER);

                    for (int i = 0; i < adapter.getCount(); i++) {

                        if (adapter.getItem(i).equals(apartmentName)) {
                            apartmentNameSpinner.setSelection(i);
                            break;
                        }
                    }


                }

                @Override
                public void onPostAddResponse(String response, DialogFragment dialogFragment) {

                }
            });


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    public boolean validateNotes() {

        EditText notesText = (EditText) pageView.findViewById(R.id.notesText);
        String notes = notesText.getText().toString();

        String WHITELIST = "[^0-9A-Za-z. ]+";
        Pattern p = Pattern.compile(WHITELIST);
        Matcher matcher = p.matcher(notes);

        if (matcher.find()) {

            errorQueue.add(Alerts.errors.get(8));
            return false;

        }
        return true;

    }

    public boolean validateSpinners() {


        if (noOfVacanciesSpinner.getSelectedItem() == null
                || noOfVacanciesSpinner.getSelectedItem() == null
                || apartmentNameSpinner.getSelectedItem() == null
                || noOfRoomsSpinner.getSelectedItem() == null
                || apartmentTypeSpinner.getSelectedItem() == null) {

            errorQueue.add(Alerts.errors.get(2));
            errorQueue.add(Alerts.errors.get(3));

            return false;

        }

        return true;
    }

    private boolean validateCostOfLiving() {

        EditText Field = (EditText) pageView
                .findViewById(R.id.costOfLivingPerMonth);
        String cost = Field.getText().toString();

        if (cost.equals(null) || cost.equals("")) {
            errorQueue.add(Alerts.errors.get(5));
            return false;


        } else if (Integer.parseInt(cost) <= 0) {
            errorQueue.add(Alerts.errors.get(4) + " " + cost + ".");
            return false;
        }

        return true;

    }


    // response from the accommodation Add Post
    @Override
    public void onResponse(String response) {

        if (response.equals(SAConstants.SUCCESS)) {
            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, SAConstants.SUCCESSFULLY_POSTED);

            AccommodationPosted alert = new AccommodationPosted();
            alert.setArguments(b);
            alert.show(getSupportFragmentManager(), "");
        } else {
            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, Alerts.errors.get(15));

            AccommodationPosted alert = new AccommodationPosted();
            alert.setArguments(b);
            alert.show(getSupportFragmentManager(), "");
        }
    }


    // callback method from AlertDialogDismiss.java
    // This closes this activity after the accommodation add has been posted
    public void closeActivity(boolean result) {

        if (result) {
            Intent returnIntent = new Intent();
            setResult(2, returnIntent);
        }

        PostAccomodationActivity.super.onBackPressed();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        L.m("spinner position=" + apartmentNameSpinner.getSelectedItemPosition());

        outState.putInt(SAConstants.APARTMENT_NAME_POSITION, apartmentNameSpinner.getSelectedItemPosition());
        outState.putStringArrayList(SAConstants.APARTMENT_NAME, mApartmentNames);

        ArrayList<String> selectedFilePaths = new ArrayList<String>();

        for (String filePath : filePaths) {
            selectedFilePaths.add(filePath);
        }

        outState.putStringArrayList(SAConstants.IMAGE_FILE_PATHS, selectedFilePaths);


    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


}

