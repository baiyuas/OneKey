package com.baiyu.share.base;

import android.content.Intent;

import com.baiyu.share.OkShareMessage;

/**
 * @author lpc
 */
public interface IShare {

    void share(OkShareMessage message);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
