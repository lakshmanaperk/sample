package com.perk.perksdk.appsaholic.v1.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>Error Data</h1>
 */
public class ErrorData implements Parcelable {

    /**
     * "data": {
     * "errors": {
     * "appsaholic_id": ["The appsaholic id field is required."]
     * }
     * }
     */

    public final ErrorCollection errors;

    protected ErrorData(Parcel in) {
        errors = in.readParcelable(null);
    }

    public static final Creator<ErrorData> CREATOR = new Creator<ErrorData>() {
        @Override
        public ErrorData createFromParcel(Parcel in) {
            return new ErrorData(in);
        }

        @Override
        public ErrorData[] newArray(int size) {
            return new ErrorData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(errors, 0);
    }
}
