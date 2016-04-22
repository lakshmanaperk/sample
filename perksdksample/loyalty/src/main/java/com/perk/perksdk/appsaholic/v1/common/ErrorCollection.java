package com.perk.perksdk.appsaholic.v1.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class ErrorCollection implements Parcelable {

    /**
     * "errors": {
     * "appsaholic_id": ["The appsaholic id field is required."]
     * }
     */

    public final ArrayList<HashMap<String, String[]>> errors = new ArrayList<>();

    protected ErrorCollection(Parcel in) {
        HashMap read = new HashMap<>();
        try {
            while (true) {
                read = in.readHashMap(null);
                errors.add(read);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Creator<ErrorCollection> CREATOR = new Creator<ErrorCollection>() {
        @Override
        public ErrorCollection createFromParcel(Parcel in) {
            return new ErrorCollection(in);
        }

        @Override
        public ErrorCollection[] newArray(int size) {
            return new ErrorCollection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (HashMap<String, String[]> hashMap : errors) {
            dest.writeValue(hashMap);
        }
    }
}
