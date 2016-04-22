package com.perk.perksdk.appsaholic.v1.points;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>PointsData</h1>
 */
public class PointsData implements Parcelable {

    public final int available_perks;

    protected PointsData(Parcel in) {
        available_perks = in.readInt();
    }

    public static final Creator<PointsData> CREATOR = new Creator<PointsData>() {
        @Override
        public PointsData createFromParcel(Parcel in) {
            return new PointsData(in);
        }

        @Override
        public PointsData[] newArray(int size) {
            return new PointsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(available_perks);
    }
}
