package com.baiyu.share.base;

import android.content.Context;
import android.content.Intent;

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

}
