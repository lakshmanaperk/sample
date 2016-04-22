package com.perk.perksdk.appsaholic.v1.id;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1></h1>
 */
public class IdUser implements Parcelable {

    /**
     * "user": {
     * "id": "26df80db-b08c-48d2-b4ea-ec7e2e112ce0",
     * "email": "victoru@fullsail.edu",
     * "first_name": "Kalu",
     * "last_name": "Ude",
     * "available_points": 676,
     * "pending_points": 0
     * }
     */

    final String id;
    final String email;
    final String first_name;
    final String last_name;
    final int available_points;
    final int pending_points;
    final String profile_image;

    protected IdUser(Parcel in) {
        id = in.readString();
        email = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        available_points = in.readInt();
        pending_points = in.readInt();
        profile_image = in.readString();
    }

    public static final Creator<IdUser> CREATOR = new Creator<IdUser>() {
        @Override
        public IdUser createFromParcel(Parcel in) {
            return new IdUser(in);
        }

        @Override
        public IdUser[] newArray(int size) {
            return new IdUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeInt(available_points);
        dest.writeInt(pending_points);
        dest.writeString(profile_image);
    }
}
