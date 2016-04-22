package com.perk.perksdk.appsaholic.v1.unclaimed;

import android.os.Parcel;
import android.os.Parcelable;

public class UnclaimedData implements Parcelable {

    public final int unclaimed_events;

    protected UnclaimedData(Parcel in) {
        unclaimed_events = in.readInt();
    }

    public static final Creator<UnclaimedData> CREATOR = new Creator<UnclaimedData>() {
        @Override
        public UnclaimedData createFromParcel(Parcel in) {
            return new UnclaimedData(in);
        }

        @Override
        public UnclaimedData[] newArray(int size) {
            return new UnclaimedData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(unclaimed_events);
    }
}
