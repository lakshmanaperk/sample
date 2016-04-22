package com.perk.perksdk.appsaholic.v1.init;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Provider;

/**
 * <h1>InitData</h1>
 */
public class InitData implements Parcelable {

    public static final Creator<InitData> CREATOR = new Creator<InitData>() {
        @Override
        public InitData createFromParcel(Parcel in) {
            return new InitData(in);
        }

        @Override
        public InitData[] newArray(int size) {
            return new InitData[size];
        }
    };
    /**
     * "data": {
     * "sdk_status": true,
     * "user_id": null,
     * "test_mode": false,
     * "app_name": "Appsaholic SDK Publisher Demo",
     * "appsaholic_id": "1f5fdaa6-17cf-4012-bd3a-856dd46c7218",
     * "ad_pod": {
     * "batch_size": 1
     * },
     * "is_new": false,
     * "video_ad_tags": [{
     * "partner": "AER",
     * "tag": "1000741"
     * }],
     * "notification_ad_tags": [{
     * "partner": "AER",
     * "tag": "1000741"
     * }],
     * "survey_ad_tags": [{
     * "partner": "DPN",
     * "tag": null
     * }],
     * "display_ad_tags": [{
     * "partner": "AER",
     * "tag": "1000741"
     * }],
     * "ad_tags": {
     * "AER": "1000741",
     * "VGL": null
     * },
     * "passive": true,
     * "passive_countdown": 3
     * }
     */

    boolean sdk_status;
    final String user_id;
    final boolean test_mode;
    final String app_name;
    final String appsaholic_id;
    final AdPod ad_pod;
    final boolean is_new;
    final Provider[] video_ad_tags;
    final Provider[] notification_ad_tags;
    final Provider[] survey_ad_tags;
    final Provider[] display_ad_tags;
    final AdTag ad_tags;
    final boolean passive;
    final int passive_countdown;

    protected InitData(Parcel in) {
        sdk_status = in.readByte() != 0;
        user_id = in.readString();
        test_mode = in.readByte() != 0;
        app_name = in.readString();
        appsaholic_id = in.readString();
        ad_pod = in.readParcelable(AdPod.class.getClassLoader());
        is_new = in.readByte() != 0;
        video_ad_tags = Provider.getProviders(in);
        notification_ad_tags = Provider.getProviders(in);
        survey_ad_tags = Provider.getProviders(in);
        display_ad_tags = Provider.getProviders(in);
        ad_tags = in.readParcelable(AdTag.class.getClassLoader());
        passive = in.readByte() != 0;
        passive_countdown = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (sdk_status ? 1 : 0));
        dest.writeString(user_id);
        dest.writeByte((byte) (test_mode ? 1 : 0));
        dest.writeString(app_name);
        dest.writeString(appsaholic_id);
        dest.writeParcelable(ad_pod, 0);
        dest.writeByte((byte) (is_new ? 1 : 0));
        dest.writeParcelableArray(video_ad_tags, 0);
        dest.writeParcelableArray(notification_ad_tags, 0);
        dest.writeParcelableArray(survey_ad_tags, 0);
        dest.writeParcelableArray(display_ad_tags, 0);
        dest.writeParcelable(ad_tags, 0);
        dest.writeByte((byte) (passive ? 1 : 0));
        dest.writeInt(passive_countdown);
    }
}
