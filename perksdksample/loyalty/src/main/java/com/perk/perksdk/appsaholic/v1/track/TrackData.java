package com.perk.perksdk.appsaholic.v1.track;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Provider;

/**
 * <h1>TrackData</h1>
 */
public class TrackData implements Parcelable {

    public final Provider[] load_ad_tags;
    public final Provider[] video_ad_tags;
    public final Provider[] notification_ad_tags;
    public final Provider[] survey_ad_tags;
    public final Provider[] display_ad_tags;

    protected TrackData(Parcel in) {
        load_ad_tags = Provider.getProviders(in);
        video_ad_tags = Provider.getProviders(in);
        notification_ad_tags = Provider.getProviders(in);
        survey_ad_tags = Provider.getProviders(in);
        display_ad_tags = Provider.getProviders(in);
    }

    public static final Creator<TrackData> CREATOR = new Creator<TrackData>() {
        @Override
        public TrackData createFromParcel(Parcel in) {
            return new TrackData(in);
        }

        @Override
        public TrackData[] newArray(int size) {
            return new TrackData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(load_ad_tags, 0);
        dest.writeParcelableArray(video_ad_tags, 0);
        dest.writeParcelableArray(notification_ad_tags, 0);
        dest.writeParcelableArray(survey_ad_tags, 0);
        dest.writeParcelableArray(display_ad_tags, 0);
    }
}
