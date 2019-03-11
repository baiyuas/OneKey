package com.baiyu.share;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lpc
 * <p>
 * 封装的分享消息实体类
 */
public class OkShareMessage implements Parcelable {

    public static final int SHARE_MIDIA_TYPE_IMAGE = 1;
    public static final int SHARE_MIDIA_TYPE_WEB = 2;

    /**
     * 分享的标题
     */
    private String title;

    /**
     * 分享的内容
     */
    private String subTitle;

    /**
     * 分享链接
     */
    private String url;

    /**
     * 分享的图片地址
     */
    private String imageUrl;

    /**
     * 1 图片， 2 图文
     */
    private int mediaType;

    private String appNameQq;

    public OkShareMessage() {
    }


    protected OkShareMessage(Parcel in) {
        title = in.readString();
        subTitle = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        mediaType = in.readInt();
        appNameQq = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeInt(mediaType);
        dest.writeString(appNameQq);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OkShareMessage> CREATOR = new Creator<OkShareMessage>() {
        @Override
        public OkShareMessage createFromParcel(Parcel in) {
            return new OkShareMessage(in);
        }

        @Override
        public OkShareMessage[] newArray(int size) {
            return new OkShareMessage[size];
        }
    };

    public String getAppNameQq() {
        return appNameQq;
    }

    public void setAppNameQq(String appNameQq) {
        this.appNameQq = appNameQq;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

}
