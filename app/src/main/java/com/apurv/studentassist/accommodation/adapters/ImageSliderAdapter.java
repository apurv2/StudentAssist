package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.internet.Network;
import com.apurv.studentassist.util.SAConstants;

import java.io.File;
import java.util.List;

/**
 * Created by akamalapuri on 8/31/2016.
 */
public class ImageSliderAdapter extends PagerAdapter {


    private ImageLoader mImageLoader;
    private Network network;

    private List mImages;
    private LayoutInflater inflater;
    private Context context;
    private String imageType;


    public ImageSliderAdapter(Context context, List mImages, String type) {
        this.context = context;
        this.mImages = mImages;
        inflater = LayoutInflater.from(context);
        network = Network.getNetworkInstnace();
        mImageLoader = network.getmImageLoader();
        this.imageType = type;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.acc_photo_view, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.accommodationImage);

        loadImages(imageView, (String) mImages.get(position));

        //  imageView.setImageResource(mImages.get(position));

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    private void loadImages(ImageView imageView, String url) {

        if (SAConstants.CLOUDINARY_IMAGES.equals(imageType)) {
            mImageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                    Bitmap photo = response.getBitmap();
                    if (photo != null) {
                        imageView.setImageBitmap(photo);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } else {

            File file = null;
            file = new File(url);

               if (file != null && file.exists()) {


                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);

            }


        }


    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}