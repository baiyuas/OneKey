package com.baiyu.share.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baiyu.share.OkShareMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lpc
 */
public abstract class BaseShare implements IShare {

    protected Context context;
    protected static final String ASSETS_PREFIX = "file:///android_assets/";

    public BaseShare(Context context) {
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected boolean isNetUrl(String url) {
        return url != null && (url.startsWith("http") || url.startsWith("https"));
    }

    protected boolean isAssetsFile(String url) {
        return url != null && url.startsWith(ASSETS_PREFIX);
    }


    /**
     * 将asset文件写到本地
     * @return
     */
    protected void writeAssetsImageToLocal(OkShareMessage message, String filePath) {
        try {
            String imageUrl = message.getImageUrl();
            String fileName = imageUrl.substring(ASSETS_PREFIX.length());
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(fileName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(filePath));
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
