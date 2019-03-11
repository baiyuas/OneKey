package com.baiyu.onekey.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.baiyu.share.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author lpc
 * <p>
 * 微信回调
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String APP_ID = "wxcd7685a4558c2592";
    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, false);
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
    public void onResp(BaseResp resp) {
        int result;

        switch (resp.errCode) {
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
