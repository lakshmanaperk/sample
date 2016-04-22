package com.perk.perksdk.appsaholic.v1.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>Appsaholic Error</h1>
 * Error data retrieved from a failed Appsaholic API request
 */
public class Error implements Parcelable {
    /**
     * {
     * "status": "fail",
     * "message": "One or more inputs is invalid",
     * "data": {
     * "errors": {
     * "appsaholic_id": ["The appsaholic id field is required."]
     * }
     * }
     * }
     */

    public final String status;
    public final String message;
    public final ErrorData data;

    protected Error(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(null);
    }

    public static final Creator<Error> CREATOR = new Creator<Error>() {
        @Override
        public Error createFromParcel(Parcel in) {
            return new Error(in);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeParcelable(data, flags);
    }
}
