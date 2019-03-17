package com.baiyu.share.alipay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.alipay.share.sdk.openapi.APAPIFactory;
import com.alipay.share.sdk.openapi.APImageObject;
import com.alipay.share.sdk.openapi.APMediaMessage;
import com.alipay.share.sdk.openapi.APWebPageObject;
import com.alipay.share.sdk.openapi.IAPApi;
import com.alipay.share.sdk.openapi.SendMessageToZFB;
import com.baiyu.share.ImageAsyncTask;
import com.baiyu.share.OKShareUtil;
import com.baiyu.share.OkShareMessage;
import com.baiyu.share.base.BaseShare;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lpc
 */
public class AlipayShare extends BaseShare {

    private static final String APP_ID = "2019031763561290";
    private static final int MIN_SUPPORT_LIFE_VERSION = 84;
    private static final int MAX_SUPPORT_LIFE_VERSION = 101;
    private static final int MAX_THUMB = 32 * 1024;

    /**
     * 1生活圈，0好友
     */
    private int channel;
    private IAPApi api;

    public AlipayShare(Context context, int channel) {
        super(context);
        this.channel = channel;
        api = APAPIFactory.createZFBApi(context.getApplicationContext(), APP_ID, true);
    }

    @Override
    public void share(OkShareMessage message) {
        if (!api.isZFBAppInstalled()) {
            OKShareUtil.toast(context, "检测您未安装支付宝");
            return;
        }

        if (!api.isZFBSupportAPI()) {
            OKShareUtil.toast(context, "当前版本支付宝不支持分享");
            return;
        }

        String filePath = Environment.getExternalStorageDirectory() + "/Android/temp.png";
        if (isAssetsFile(message.getImageUrl())) {
            try {
                String imageUrl = message.getImageUrl();
                String fileName = imageUrl.substring(ASSETS_PREFIX.length());
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(fileName));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(filePath));
                bitmap.recycle();
                message.setImageUrl(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (message.getMediaType() == 1) {
            //初始化一个APImageObject对象，组装图片内容对象
            APImageObject imageObject = new APImageObject();
            if (isNetUrl(message.getImageUrl())) {
                imageObject.imageUrl = message.getImageUrl();
            } else {
                imageObject.setImagePath(message.getImageUrl());
            }

            //初始化一个APMediaMessage对象 ，组装分享消息对象
            APMediaMessage mediaMessage = new APMediaMessage();
            mediaMessage.mediaObject = imageObject;

            //将分享消息对象包装成请求对象
            SendMessageToZFB.Req req = new SendMessageToZFB.Req();
            req.message = mediaMessage;
            req.transaction = "ImageShare" + String.valueOf(System.currentTimeMillis());
            if (isSupportLife()) {
                req.scene = SendMessageToZFB.Req.ZFBSceneTimeLine;
            }
            //发送请求
            api.sendReq(req);
            return;
        }

        ImageAsyncTask.start(message.getImageUrl(), new ImageAsyncTask.AsyncTaskCallBack() {
            @Override
            public void success(Bitmap bitmap) {
                dealShareRequest(bitmap, message);
            }

            @Override
            public void fail() {
                OKShareUtil.toast(context, "加载分享图片失败");
            }
        });
    }

    private void dealShareRequest(Bitmap bitmap, OkShareMessage message) {
        //初始化一个APWebPageObject对象，组装网页Card内容对象
        APWebPageObject webPageObject = new APWebPageObject();
        webPageObject.webpageUrl = message.getUrl();

        ////初始化APMediaMessage ，组装分享消息对象
        APMediaMessage webMessage = new APMediaMessage();
        webMessage.mediaObject = webPageObject;
        webMessage.title = message.getTitle();
        webMessage.description = message.getSubTitle();
        //网页缩略图的分享支持bitmap和url两种方式，直接通过bitmap传递时bitmap最大为32K
        int imgSize = bitmap.getByteCount();
        if (imgSize > MAX_THUMB) {
            //设置缩略图
            Bitmap thumbBmp = OKShareUtil.createBitmapThumbnail(bitmap);
            bitmap.recycle();
            Bitmap noWithBorder = OKShareUtil.changeColor(thumbBmp);
            webMessage.thumbData = OKShareUtil.bmpToByteArray(noWithBorder, true);
        } else {
            Bitmap noWithBorder = OKShareUtil.changeColor(bitmap);
            webMessage.thumbData = OKShareUtil.bmpToByteArray(noWithBorder, true);
        }
        //将分享消息对象包装成请求对象
        SendMessageToZFB.Req webReq = new SendMessageToZFB.Req();
        webReq.message = webMessage;
        webReq.transaction = "WebShare" + String.valueOf(System.currentTimeMillis());
        if (isSupportLife()) {
            webReq.scene = SendMessageToZFB.Req.ZFBSceneTimeLine;
        }
        //发送请求
        api.sendReq(webReq);
    }

    private boolean isSupportLife() {
        // 84-101版本支持生活圈， >101生活圈会在支付宝内部用户选择
        if (api.getZFBVersionCode() >= MAX_SUPPORT_LIFE_VERSION) {
            return false;
        } else {
            return channel == 1 && api.getZFBVersionCode() >= MIN_SUPPORT_LIFE_VERSION;
        }
    }
}
