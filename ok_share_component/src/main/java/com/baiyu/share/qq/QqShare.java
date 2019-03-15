package com.baiyu.share.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

import com.baiyu.share.ImageAsyncTask;
import com.baiyu.share.OKShareUtil;
import com.baiyu.share.OkShareMessage;
import com.baiyu.share.base.BaseShare;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author lpc
 */
public class QqShare extends BaseShare {

    private final static String APP_ID = "100827287";

    /**
     * 1 QQ, 2QQ-Zone
     */
    private int channel;
    private Tencent tencent;

    private IUiListener listener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            OKShareUtil.toast(context, "分享成功");
        }

        @Override
        public void onError(UiError uiError) {
            OKShareUtil.toast(context, "分享失败:" + uiError.errorMessage);
        }

        @Override
        public void onCancel() {
            OKShareUtil.toast(context, "取消分享");
        }
    };

    public QqShare(Context context, int channel) {
        super(context);
        this.channel = channel;
        tencent = Tencent.createInstance(APP_ID, context.getApplicationContext());
    }

    @Override
    public void share(OkShareMessage message) {
//        if (!tencent.isQQInstalled(context)) {
//            OKShareUtil.toast(context, "检测到您未安装QQ");
//            return;
//        }
        String filePath = Environment.getExternalStorageDirectory() + "/Android/temp.png";
        if (isAssetsFile(message.getImageUrl())) {
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

        try {
            if (channel == 1) {
                // 图片
                if (message.getMediaType() == 1) {
                    Bundle params = new Bundle();
                    if (isNetUrl(message.getImageUrl())) {
                        shareImage(message);
                        return;
                    } else if (isAssetsFile(message.getImageUrl())) {
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                    } else {
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, message.getImageUrl());
                    }
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, message.getAppNameQq());
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                    tencent.shareToQQ((Activity) context, params, listener);
                    return;
                }

                if (isAssetsFile(message.getImageUrl())) {
                    message.setImageUrl(filePath);
                }
                Bundle params = buildQqShareParams(message);
                tencent.shareToQQ((Activity) context, params, listener);
            } else {

                if (message.getMediaType() == 1) {
                    Bundle params = new Bundle();
                    ArrayList<String> imageUrls = new ArrayList<>();
                    if (isNetUrl(message.getImageUrl())) {
                        shareImageZone(message);
                        return;
                    } else if (isAssetsFile(message.getImageUrl())) {
                        imageUrls.add(filePath);
                    } else {
                        imageUrls.add(message.getImageUrl());
                    }
                    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, message.getTitle());
                    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, message.getSubTitle());
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                    tencent.publishToQzone((Activity) context, params, listener);
                    return;
                }

                if (isAssetsFile(message.getImageUrl())) {
                    message.setImageUrl(filePath);
                }
                Bundle params = buildQqZoneShareParams(message);
                tencent.shareToQzone((Activity) context, params, listener);
            }
        } catch (Exception e) {
            OKShareUtil.toast(context, "请传入Activity");
        }
    }


    private void shareImage(OkShareMessage message) {
        ImageAsyncTask.start(message.getImageUrl(), new ImageAsyncTask.AsyncTaskCallBack() {
            @Override
            public void success(Bitmap bitmap) {
                Bundle params = new Bundle();
                String filePath = Environment.getExternalStorageDirectory() + "/Android/temp.png";
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(filePath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.recycle();
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, message.getAppNameQq());
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                tencent.shareToQQ((Activity) context, params, listener);
            }

            @Override
            public void fail() {
                OKShareUtil.toast(context, "分享图片加载失败");
            }
        });
    }

    private void shareImageZone(OkShareMessage message) {
        ImageAsyncTask.start(message.getImageUrl(), new ImageAsyncTask.AsyncTaskCallBack() {
            @Override
            public void success(Bitmap bitmap) {
                Bundle params = new Bundle();
                ArrayList<String> imageUrls = new ArrayList<>();
                String filePath = Environment.getExternalStorageDirectory() + "/Android/temp.png";
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(filePath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.recycle();
                imageUrls.add(filePath);
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, message.getTitle());
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, message.getSubTitle());
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                tencent.publishToQzone((Activity) context, params, listener);
            }

            @Override
            public void fail() {
                OKShareUtil.toast(context, "分享图片加载失败");
            }
        });
    }

    private Bundle buildQqShareParams(OkShareMessage message) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, message.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, message.getUrl());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, message.getSubTitle());

        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, message.getAppNameQq());

        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        String imageUrl = message.getImageUrl();
        if (!OKShareUtil.checkNull(imageUrl)) {
            params.putString((imageUrl.startsWith("http") || imageUrl.startsWith("https")) ? QQShare.SHARE_TO_QQ_IMAGE_URL : QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, message.getImageUrl());
        }
        return params;
    }

    private Bundle buildQqZoneShareParams(OkShareMessage message) {
        Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, message.getTitle());
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, message.getUrl());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, message.getSubTitle());
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, message.getAppNameQq());

        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        ArrayList<String> imageUrls = new ArrayList<String>();
        String imageUrl = message.getImageUrl();
        imageUrls.add(imageUrl);
        params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);


        params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        return params;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, listener);
    }
}
