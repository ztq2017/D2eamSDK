package com.overseas.exports;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 游戏SDK配置文件
 */
public class SdkInitSetting implements Parcelable {
    private String channelId; //游戏代码
    private String appKey; //游戏金钥
    private String language;// 字体语言


    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.channelId);
        parcel.writeString(this.appKey);
        parcel.writeString(this.language);

    }

    public static final Parcelable.Creator<SdkInitSetting> CREATOR = new Parcelable.Creator<SdkInitSetting>() {

        public SdkInitSetting createFromParcel(Parcel source) {

            SdkInitSetting sdkInitSetting = new SdkInitSetting();
            sdkInitSetting.channelId = source.readString();
            sdkInitSetting.appKey = source.readString();
            sdkInitSetting.language = source.readString();
            return sdkInitSetting;
        }

        public SdkInitSetting[] newArray(int size) {
            return new SdkInitSetting[size];
        }
    };

}
