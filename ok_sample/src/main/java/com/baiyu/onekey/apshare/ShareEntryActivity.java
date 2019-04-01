package com.baiyu.onekey.apshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.alipay.share.sdk.openapi.APAPIFactory;
import com.alipay.share.sdk.openapi.BaseReq;
import com.alipay.share.sdk.openapi.BaseResp;
import com.alipay.share.sdk.openapi.IAPAPIEventHandler;
import com.alipay.share.sdk.openapi.IAPApi;
import com.baiyu.share.BuildConfig;
import com.baiyu.share.R;

/**
 * @author lpc
 */
public class ShareEntryActivity extends Activity implements IAPAPIEventHandler {

    private IAPApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = APAPIFactory.createZFBApi(getApplicationContext(), BuildConfig.Apliay_ID, true);
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        int result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.ok_share_error_code_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.ok_share_error_code_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.ok_share_error_code_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.ok_share_error_code_unsupported;
                break;
            default:
                result = R.string.ok_share_error_code_unknown;
                break;
        }

        Toast.makeText(this, getString(result), Toast.LENGTH_SHORT).show();
        finish();
        overridePendingTransition(0, 0);
    }
}
