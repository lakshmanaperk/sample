package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.PerkManager;

/**
 * <h1>EmptyClaimEvent</h1>
 * A dummy class providing all of the functions of an {@link ClaimEvent} while doing nothing.
 */
public class EmptyClaimEvent extends ClaimEvent implements Parcelable {
    EmptyClaimEvent(Context context, String eventId) {
        super(context, eventId);
    }

    protected EmptyClaimEvent(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAdSource());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmptyClaimEvent> CREATOR = new Creator<EmptyClaimEvent>() {
        @Override
        public EmptyClaimEvent createFromParcel(Parcel in) {
            return new EmptyClaimEvent(in);
        }

        @Override
        public EmptyClaimEvent[] newArray(int size) {
            return new EmptyClaimEvent[size];
        }
    };

    @Override
    public void end() {
        // The event ID is not valid so don't attempt to call the API
        // but do provide feedback to the app.
        PerkManager.notifyApp("Can't end event with ID \"" + getId() + "\"");
    }
}
