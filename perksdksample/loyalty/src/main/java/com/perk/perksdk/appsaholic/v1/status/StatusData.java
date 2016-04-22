package com.perk.perksdk.appsaholic.v1.status;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>StatusData</h1>
 */
public class StatusData implements Parcelable {

    public final boolean status;

    protected StatusData(Parcel in) {
        status = in.readByte() != 0;
    }

    public static final Creator<StatusData> CREATOR = new Creator<StatusData>() {
        @Override
        public StatusData createFromParcel(Parcel in) {
            return new StatusData(in);
        }

        @Override
        public StatusData[] newArray(int size) {
            return new StatusData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
    }
}
