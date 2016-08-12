package com.apurv.studentassist.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.internet.Network;

import java.net.URL;


public class TestFragment2 extends Fragment {

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_test_fragment2, container, false);

            new getProfilepicture().execute();

        return mView;


    }

    private void loadImages(String urlThumbnail) {

        ImageLoader mImageLoader;
        Network network = Network.getNetworkInstnace();
        mImageLoader=network.getmImageLoader();


            mImageLoader.get("https://graph.facebook.com/" + "10205308030011466" + "/picture?type=large", new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                    ImageView mImg;
                    mImg = (ImageView) mView.findViewById(R.id.imageViewAppugadu);
                    mImg.setImageBitmap(response.getBitmap());


                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

    }


    private class getProfilepicture extends AsyncTask<Void,Void,Bitmap>
    {



        @Override
        protected Bitmap doInBackground(Void... params) {

            URL imageURL=null;
            Bitmap bitmap=null;
            String userID="10205308030011466";

            try {


                imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());



            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;




        }

        @Override
        protected void onPostExecute(Bitmap aVoid) {

            ImageView mImg;
            mImg = (ImageView) mView.findViewById(R.id.imageViewAppugadu);
            mImg.setImageBitmap(aVoid);





        }
    }

}


