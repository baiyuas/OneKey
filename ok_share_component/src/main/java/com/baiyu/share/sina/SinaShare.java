package com.baiyu.share.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.baiyu.share.ImageAsyncTask;
import com.baiyu.share.OKShareUtil;
import com.baiyu.share.OkShareMessage;
import com.baiyu.share.base.BaseShare;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lpc
 * <p>
 * https://github.com/sinaweibosdk/weibo_android_sdk
 * https://open.weibo.com/wiki/Android_SDK%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3
 */
public class SinaShare extends BaseShare {

    private static final int MAX_THUMB = 32 * 1024;
    private static final String APP_ID = "1616848249";
//    private static final String APP_Secret = "76f3dd9a055ccbaa84cbadb16e4afa0f";

    private WbShareHandler shareHandler;

    private WbShareCallback uiCallBack = new WbShareCallback() {
        @Override
        public void onWbShareSuccess() {
            OKShareUtil.toast(context, "分享成功");
        }

        @Override
        public void onWbShareCancel() {
            OKShareUtil.toast(context, "分享取消");
        }

        @Override
        public void onWbShareFail() {
            OKShareUtil.toast(context, "分享失败");
        }
    };

    public SinaShare(Context context) {
        super(context);
        WbSdk.install(context, new AuthInfo(context, APP_ID, "https://api.weibo.com/oauth2/default.html", ""));
        shareHandler = new WbShareHandler((Activity) context);
        shareHandler.registerApp();
    }

    @Override
    public void share(OkShareMessage message) {
        if (!WbSdk.isWbInstall(context)) {
            OKShareUtil.toast(context, "检测到您未安装新浪微博客户端");
            return;
        }

        String filePath = Environment.getExternalStorageDirectory() + "/Android/temp.png";
        if (isAssetsFile(message.getImageUrl())) {
            writeAssetsImageToLocal(message, filePath);
        }

        if (message.getMediaType() == OkShareMessage.SHARE_MIDIA_TYPE_WEB) {
            ImageAsyncTask.start(message.getImageUrl(), new ImageAsyncTask.AsyncTaskCallBack() {
                @Override
                public void success(Bitmap bitmap) {
                    shareWebPageObj(message, bitmap);
                }

                @Override
                public void fail() {
                    OKShareUtil.toast(context, "加载分享图片失败");
                }
            });
        }

        if (message.getMediaType() == OkShareMessage.SHARE_MIDIA_TYPE_IMAGE) {
            if (isNetUrl(message.getImageUrl())) {
                ImageAsyncTask.start(message.getImageUrl(), new ImageAsyncTask.AsyncTaskCallBack() {
                    @Override
                    public void success(Bitmap bitmap) {
                        shareImageObj(bitmap);
                    }

                    @Override
                    public void fail() {
                        OKShareUtil.toast(context, "加载分享图片失败");
                    }
                });
            } else if (isAssetsFile(message.getImageUrl())) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                shareImageObj(bitmap);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(message.getImageUrl());
                shareImageObj(bitmap);
            }
        }

    }


    private void shareImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        multiMessage.imageObject = imageObject;
        shareHandler.shareMessage(multiMessage, false);
    }

    /**
     * Web类型分享
     *
     * @param message
     * @param bitmap
     */
    private void shareWebPageObj(OkShareMessage message, Bitmap bitmap) {
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        multiMessage.imageObject = imageObject;

        TextObject textObject = new TextObject();
        textObject.text = message.getSubTitle();
        multiMessage.textObject = textObject;

        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = message.getTitle();
        mediaObject.description = message.getSubTitle();
        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        int imgSize = bitmap.getByteCount();
        if (imgSize > MAX_THUMB) {
            //设置缩略图
            Bitmap thumbBmp = OKShareUtil.createBitmapThumbnail(bitmap);
            bitmap.recycle();
            Bitmap noWithBorder = OKShareUtil.changeColor(thumbBmp);
            mediaObject.setThumbImage(noWithBorder);
        } else {
            Bitmap noWithBorder = OKShareUtil.changeColor(bitmap);
            mediaObject.setThumbImage(noWithBorder);
        }
        mediaObject.actionUrl = message.getUrl();
        mediaObject.defaultText = message.getSubTitle();
        multiMessage.mediaObject = mediaObject;
        shareHandler.shareMessage(multiMessage, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        shareHandler.doResultIntent(data, uiCallBack);
    }
}
