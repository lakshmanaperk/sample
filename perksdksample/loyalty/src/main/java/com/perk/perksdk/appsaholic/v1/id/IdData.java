package com.perk.perksdk.appsaholic.v1.id;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>IdData</h1>
 */
public class IdData implements Parcelable {

    final IdUser user;

    protected IdData(Parcel in) {
        user = in.readParcelable(IdUser.class.getClassLoader());
    }

    public static final Creator<IdData> CREATOR = new Creator<IdData>() {
        @Override
        public IdData createFromParcel(Parcel in) {
            return new IdData(in);
        }

        @Override
        public IdData[] newArray(int size) {
            return new IdData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, 0);
    }
}
