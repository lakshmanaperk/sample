package com.perk.perksdk.appsaholic.v1.init;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>AdTag</h1>
 */
public class AdTag implements Parcelable {

    public static final Creator<AdTag> CREATOR = new Creator<AdTag>() {
        @Override
        public AdTag createFromParcel(Parcel in) {
            return new AdTag(in);
        }

        @Override
        public AdTag[] newArray(int size) {
            return new AdTag[size];
        }
    };

    public final String AER;
    public final String VGL;

    protected AdTag(Parcel in) {
        AER = in.readString();
        VGL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AER);
        dest.writeString(VGL);
    }
}
