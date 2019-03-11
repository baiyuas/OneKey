package com.baiyu.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import static com.baiyu.share.OkShareFragment.ARG_SHARE_MESSAGE;
import static com.baiyu.share.OkShareFragment.ARG_SHARE_OPTIONS;

/**
 * @author lpc
 */
public class OneKeyShare {

    private volatile static OneKeyShare oneKeyShare;

    private OkShareMessage message;
    private ArrayList<OkShareOption> shareOptions;

    private OkShareFragment shareDialog;

    public static OneKeyShare get() {
        if (oneKeyShare == null) {
            synchronized (OneKeyShare.class) {
                oneKeyShare = new OneKeyShare();
            }
        }

        return oneKeyShare;
    }

    private OneKeyShare() {
        shareOptions = new ArrayList<>();
    }


    /**
     * 设置分享内容
     *
     * @param message
     */
    public OneKeyShare setMessage(OkShareMessage message) {
        this.message = message;
        shareOptions.clear();
        return this;
    }

    /**
     * 全局添加分享选项
     *
     * @param option
     * @return
     */
    public OneKeyShare addOption(OkShareOption option) {
        if (!shareOptions.contains(option)) {
            shareOptions.add(option);
        }
        return this;
    }


    private static final String FRAGMENT_SHARE_TAG = "fragmentShareTag";

    /**
     * 消息回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (shareDialog != null) {
            shareDialog.shareActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 展示分享
     */
    public void show(Context context) {
        if (message == null) {
            throw new IllegalArgumentException("缺少分享信息");
        }

        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;

            FragmentManager fmg = activity.getSupportFragmentManager();
            shareDialog = (OkShareFragment) fmg.findFragmentByTag(FRAGMENT_SHARE_TAG);
            if (shareDialog != null) {
                shareDialog.dismissAllowingStateLoss();
            }

            Bundle bundle = new Bundle();

            bundle.putParcelable(ARG_SHARE_MESSAGE, message);
            bundle.putParcelableArrayList(ARG_SHARE_OPTIONS, shareOptions);


            shareDialog = (OkShareFragment) Fragment.instantiate(context, OkShareFragment.class.getName(), bundle);

            fmg.beginTransaction()
                    .add(shareDialog, FRAGMENT_SHARE_TAG)
                    .commitNowAllowingStateLoss();
            return;
        }
        throw new IllegalArgumentException("context is not Activity!");
    }
}
