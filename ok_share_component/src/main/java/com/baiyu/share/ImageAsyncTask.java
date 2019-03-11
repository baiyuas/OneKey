package com.baiyu.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author lpc
 */
public class ImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    private AsyncTaskCallBack callBack;
    private String imageUrl;

    private ImageAsyncTask(String imageUrl, AsyncTaskCallBack callBack) {
        this.callBack = callBack;
        this.imageUrl = imageUrl;
    }

    public static void start(String imageUrl, AsyncTaskCallBack callBack) {
        new ImageAsyncTask(imageUrl, callBack).execute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            Bitmap bitmap;
            if (imageUrl.startsWith(HTTP)
                    || imageUrl.startsWith(HTTPS)) {
                //网络图片下载
                URL url = new URL(imageUrl);
                InputStream is = url.openStream();
                bitmap = getSampleBitmap(is);
                is.close();
            } else {
                bitmap = getSampleBitmap(new FileInputStream(imageUrl));
            }

            return bitmap;
        } catch (Exception e) {
            Log.e("Heshenghuo", "Share Load Image Error" + e.getMessage());
        }

        return null;
    }

    private Bitmap getSampleBitmap(InputStream is) throws IOException {

        BufferedInputStream stream = new BufferedInputStream(is);
        stream.mark(10 * 1024 * 1024);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        float width = options.outWidth;
        float height = options.outHeight;

        float scaleWidth = width > 480 ? 480 : width;
        float scaleHeight = scaleWidth / width * height;

        calculateInSampleSize((int) scaleWidth, (int) scaleHeight, options, true);
        stream.reset();
        return BitmapFactory.decodeStream(stream, null, options);
    }

    private void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options, boolean centerInside) {
        calculateInSampleSize(reqWidth, reqHeight, options.outWidth, options.outHeight, options,
                centerInside);
    }

    private void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                       BitmapFactory.Options options, boolean centerInside) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = centerInside
                        ? Math.max(heightRatio, widthRatio)
                        : Math.min(heightRatio, widthRatio);
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            callBack.success(bitmap);
        } else {
            callBack.fail();
        }
    }

    public interface AsyncTaskCallBack {

        void success(Bitmap bitmap);

        void fail();
    }
}
