package com.baiyu.share;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lpc
 * <p>
 * 分享选项
 */
public class OkShareOption implements Comparable<OkShareOption>, Parcelable {

    public static final int SHARE_TYPE_ALIPAY = 0x001;
    public static final int SHARE_TYPE_ALIPAY_LIFE = 0x002;
    public static final int SHARE_TYPE_WECHAT = 0x003;
    public static final int SHARE_TYPE_WECHAT_MOMENTS = 0x004;
    public static final int SHARE_TYPE_QQ = 0x005;
    public static final int SHARE_TYPE_QQ_ZONE = 0x006;
    public static final int SHARE_TYPE_SINA = 0x007;
    public static final int SHARE_TYPE_CUSTOM = 0x008;

    @DrawableRes
    private int drawable;
    private String text;
    private int sort;
    private int type;

    private CustomOptionClickListener listener;

    public OkShareOption(int drawable, String text, @ShareType int type) {
        this.drawable = drawable;
        this.text = text;
        this.type = type;
    }

    public OkShareOption(int drawable, String text, int sort, @ShareType int type) {
        this.drawable = drawable;
        this.text = text;
        this.sort = sort;
        this.type = type;
    }

    protected OkShareOption(Parcel in) {
        drawable = in.readInt();
        text = in.readString();
        sort = in.readInt();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(drawable);
        dest.writeString(text);
        dest.writeInt(sort);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OkShareOption> CREATOR = new Creator<OkShareOption>() {
        @Override
        public OkShareOption createFromParcel(Parcel in) {
            return new OkShareOption(in);
        }

        @Override
        public OkShareOption[] newArray(int size) {
            return new OkShareOption[size];
        }
    };

    public int getSort() {
        return sort;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getDrawable() {
        return drawable;
    }

    public OkShareOption setListener(CustomOptionClickListener listener) {
        this.listener = listener;
        return this;
    }

    public CustomOptionClickListener getListener() {
        return listener;
    }

    @Override
    public int compareTo(@NonNull OkShareOption o) {
        return this.sort > o.sort ? 1 : -1;
    }

    @IntDef({SHARE_TYPE_ALIPAY, SHARE_TYPE_WECHAT, SHARE_TYPE_WECHAT_MOMENTS,
            SHARE_TYPE_QQ, SHARE_TYPE_QQ_ZONE, SHARE_TYPE_SINA, SHARE_TYPE_CUSTOM,
            SHARE_TYPE_ALIPAY_LIFE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShareType {
    }



    /**
     * 自定义分享选项点击
     */
    public interface CustomOptionClickListener {
        void click();
    }
}
