package com.baiyu.onekey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baiyu.share.OkShareMessage;
import com.baiyu.share.OkShareOption;
import com.baiyu.share.OneKeyShare;

/**
 * @author baiyu
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkShareMessage message = new OkShareMessage();
        message.setTitle("分享标题");
        message.setAppNameQq("一键分享");
        message.setImageUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1552208964109&di=4b8c15a7f50ebd55be64fb967ff3ba15&imgtype=0&src=http%3A%2F%2Fwww.dzwww.com%2Fnvxing%2Ftt%2F201304%2FW020130411383037863925.jpg");
        message.setMediaType(OkShareMessage.SHARE_MIDIA_TYPE_WEB);
//        message.setMediaType(OkShareMessage.SHARE_MIDIA_TYPE_IMAGE);
        message.setSubTitle("分享内容");
        message.setUrl("http://www.baidu.com");

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            OneKeyShare.get()
                    .setMessage(message)
                    .addOption(new OkShareOption(R.drawable.ic_share_alipay, "支付宝", 1, OkShareOption.SHARE_TYPE_ALIPAY))
                    .addOption(new OkShareOption(R.drawable.ic_share_wechat, "微信", 2, OkShareOption.SHARE_TYPE_WECHAT))
                    .addOption(new OkShareOption(R.drawable.ic_share_moments, "朋友圈", 3, OkShareOption.SHARE_TYPE_WECHAT_MOMENTS))
                    .addOption(new OkShareOption(R.drawable.ic_share_zone, "QQ空间", 5, OkShareOption.SHARE_TYPE_QQ_ZONE))
                    .addOption(new OkShareOption(R.drawable.ic_share_qq, "QQ", 4, OkShareOption.SHARE_TYPE_QQ))
                    .addOption(new OkShareOption(R.drawable.ic_share_sina, "新浪", 6, OkShareOption.SHARE_TYPE_SINA))
                    .addOption(new OkShareOption(R.drawable.ic_share_ding, "钉钉", 7, OkShareOption.SHARE_TYPE_QQ))
                    .show(this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        OneKeyShare.get().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
