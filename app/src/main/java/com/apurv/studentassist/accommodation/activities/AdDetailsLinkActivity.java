package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.branch.referral.Branch;

//Using Serialization because parcel cannot be stored into shared Preferences

public class AdDetailsLinkActivity extends AppCompatActivity implements LodingDialogInterface {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_link_ad_details);


    }


    @Override
    public void onStart() {
        super.onStart();

        LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.SETTING_THINGS_UP, getSupportFragmentManager());

        Branch branch = Branch.getInstance();

        branch.initSession((referringParams, error) -> {
            if (error == null) {
                Gson gson = new Gson();
                AccommodationAdd linkedAdd = gson.fromJson(referringParams.toString(), AccommodationAdd.class);
                L.m("params==" + referringParams);

                List<String> photoIds = new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    try {
                        photoIds.add(referringParams.getString(SAConstants.PHOTO_ID + i));
                    } catch (JSONException e) {
                        ErrorReporting.logReport(e);
                    }
                }

                if (!photoIds.isEmpty()) {
                    linkedAdd.setAddPhotoIds(photoIds);
                }

                L.m("linked Add==" + linkedAdd);
                loadingDialog.dismiss();
                Intent details = new Intent(AdDetailsLinkActivity.this, AdDetailsActivity.class);

                details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) linkedAdd);
                details.putExtra(SAConstants.BRANCH_LINK, true);
                details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(details);
                finish();

            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onResponse(String response) {

    }
}





