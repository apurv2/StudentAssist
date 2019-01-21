package com.apurv.studentassist.internet;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.apurv.studentassist.base.StudentAssistApplication;

/**
 * Created by apurv on 6/3/15.
 */
public class Network {

    public static Network mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    private Network() {

        mRequestQueue = Volley.newRequestQueue(StudentAssistApplication.Companion.getAppContext());

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }


    public static Network getNetworkInstnace() {
        if (mInstance == null) {
            mInstance = new Network();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

}
