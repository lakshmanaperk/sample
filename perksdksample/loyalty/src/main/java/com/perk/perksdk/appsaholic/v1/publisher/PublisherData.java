package com.perk.perksdk.appsaholic.v1.publisher;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lakshmana on 04/03/16.
 */
public class PublisherData implements Parcelable {
    final int balance;

    protected PublisherData(Parcel in) {
        balance = in.readInt();
    }

    public static final Creator<PublisherData> CREATOR = new Creator<PublisherData>() {
        @Override
        public PublisherData createFromParcel(Parcel in) {
            return new PublisherData(in);
        }

        @Override
        public PublisherData[] newArray(int size) {
            return new PublisherData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(balance);
    }
}
