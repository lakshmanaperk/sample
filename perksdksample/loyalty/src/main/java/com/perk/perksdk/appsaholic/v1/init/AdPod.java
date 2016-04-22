package com.perk.perksdk.appsaholic.v1.init;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>AdPod</h1>
 */
public class AdPod implements Parcelable {

    public static final Creator<AdPod> CREATOR = new Creator<AdPod>() {
        @Override
        public AdPod createFromParcel(Parcel in) {
            return new AdPod(in);
        }

        @Override
        public AdPod[] newArray(int size) {
            return new AdPod[size];
        }
    };
    /**
     * "ad_pod": {
     * "batch_size": 1
     * },
     */

    public final String ad_pod;

    protected AdPod(Parcel in) {
        ad_pod = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ad_pod);
    }
}
