package com.baiyu.share;

import android.content.Context;
import android.content.Intent;

import com.baiyu.share.alipay.AlipayShare;
import com.baiyu.share.base.IShare;
import com.baiyu.share.qq.QqShare;
import com.baiyu.share.sina.SinaShare;
import com.baiyu.share.wechat.WxShare;

/**
 * @author lpc
 */
class OneKeyShareSdk {

    private Context context;
    private IShare shareChannel;

    OneKeyShareSdk(Context context) {
        this.context = context;
    }

    /**
     * 分享
     *
     * @param type
     * @param message
     */
    void share(@OkShareOption.ShareType int type, OkShareMessage message) {
        switch (type) {
            case OkShareOption.SHARE_TYPE_ALIPAY:
                shareChannel = new AlipayShare(context, 0);
                break;
            case OkShareOption.SHARE_TYPE_ALIPAY_LIFE:
                shareChannel = new AlipayShare(context, 1);
                break;
            case OkShareOption.SHARE_TYPE_QQ_ZONE:
                shareChannel = new QqShare(context, 0);
                break;
            case OkShareOption.SHARE_TYPE_QQ:
                shareChannel = new QqShare(context, 1);
                break;
            case OkShareOption.SHARE_TYPE_SINA:
                shareChannel = new SinaShare(context);
                break;
            case OkShareOption.SHARE_TYPE_WECHAT_MOMENTS:
                shareChannel = new WxShare(context, 1);
                break;
            case OkShareOption.SHARE_TYPE_WECHAT:
                shareChannel = new WxShare(context, 0);
                break;
            case OkShareOption.SHARE_TYPE_CUSTOM:
            default:
                break;
        }

        if (shareChannel != null) {
            shareChannel.share(message);
        }
    }

    /**
     * 用于QQ
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (shareChannel != null) {
            shareChannel.onActivityResult(requestCode, resultCode, data);
        }
    }
}
