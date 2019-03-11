package com.baiyu.share.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baiyu.share.ImageAsyncTask;
import com.baiyu.share.OKShareUtil;
import com.baiyu.share.OkShareMessage;
import com.baiyu.share.base.BaseShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

/**
 * @author lpc
 */
public class WxShare extends BaseShare {

    private static final String APP_ID = "wxcd7685a4558c2592";
    private static final int MAX_THUMB = 32 * 1024;
    private static final int THUMB_SIZE = 150;

    private IWXAPI api;

    /**
     * 1 朋友圈，0微信
     */
    private int channel;

    public WxShare(Context context, int channel) {
        super(context);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context.getApplicationContext(), APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
        this.channel = channel;
    }

    @Override
    public void share(OkShareMessage message) {
        if (!api.isWXAppInstalled()) {
            OKShareUtil.toast(context, "请安装微信客户端");
            return;
        }

        if (OKShareUtil.checkNull(message.getImageUrl())) {
            OKShareUtil.toast(context, "缺少分享图片链接");
            return;
        }

        if (isAssetsFile(message.getImageUrl())) {
            try {
                String imageUrl = message.getImageUrl();
                String fileName = imageUrl.substring(ASSETS_PREFIX.length());
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(fileName));
                dealShareRequest(bitmap, message);
            } catch (IOException e) {
                OKShareUtil.toast(context, "加载分享图片失败");
            }
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

    /**
     * 处理分享
     *
     * @param bitmap
     * @param message
     */
    private void dealShareRequest(Bitmap bitmap, OkShareMessage message) {
        WXMediaMessage msg = new WXMediaMessage();
        String transaction;
        if (message.getMediaType() == 1) {
            WXImageObject imgObj = new WXImageObject();
            imgObj.imageData = OKShareUtil.bmpToByteArray(bitmap, false);
            msg.mediaObject = imgObj;
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            bitmap.recycle();
            msg.thumbData = OKShareUtil.bmpToByteArray(thumbBmp, true);
            transaction = OKShareUtil.buildTransaction("img");
        } else {
            //初始化一个WXWebpageObject，填写url
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = message.getUrl();

            //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
            msg.mediaObject = webpage;
            msg.title = channel == 1 ? message.getSubTitle() : message.getTitle();
            msg.description = message.getSubTitle();

            int imgSize = bitmap.getByteCount();
            if (imgSize > MAX_THUMB) {
                //设置缩略图
                Bitmap thumbBmp = OKShareUtil.createBitmapThumbnail(bitmap);
                bitmap.recycle();
                Bitmap noWithBorder = OKShareUtil.changeColor(thumbBmp);
                msg.thumbData = OKShareUtil.bmpToByteArray(noWithBorder, true);
            } else {
                Bitmap noWithBorder = OKShareUtil.changeColor(bitmap);
                msg.thumbData = OKShareUtil.bmpToByteArray(noWithBorder, true);
            }
            transaction = OKShareUtil.buildTransaction("webpage");
        }


        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = channel;
        //调用api接口，发送数据到微信
        boolean success = api.sendReq(req);
        OKShareUtil.toast(context, success ? "分享成功" : "分享失败");
    }
}
