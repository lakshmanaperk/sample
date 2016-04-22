package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.end.End;

/**
 * <h1>AppsaholicEvent</h1>
 * todo:complete documentation
 */
public abstract class AppsaholicEvent implements Parcelable {

    private static Context sharedContext;//todo:definitely a bad idea but whatever fuck it
    private String id = "";
    private String adSource;
    protected Runnable successAction;
    protected Runnable failureAction;
    protected OnEventCompleteListener eventCompleteListener;

    /**
     * Check if an Appsaholic ID is valid
     *
     * @return true if Appsaholic ID is valid or false
     */
    public static boolean eventHasValidId(String eventId) {
        return (eventId != null) && !eventId.isEmpty();
    }

    public abstract void start();

    public abstract void end();

    /**
     * Get the event or placement id
     *
     * @return Event ID
     */
    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }


    protected static Context getContext() {
        return sharedContext;
    }

    protected static void setContext(Context context) {
        sharedContext = context;
    }

    public interface OnEventCompleteListener {
        void onEndEventSuccess(End adData);

        void onEndEventNotModified();

        void onEndEventFailure(String message);
    }

    /**
     * Set a Runnable action to perform on this claim once it is successfully loaded.
     *
     * @param action {@link Runnable} action
     */
    public AppsaholicEvent setSuccessAction(Runnable action) {
        this.successAction = action;
        return this;
    }

    public AppsaholicEvent setFailureAction(Runnable action) {
        this.failureAction = action;
        return this;
    }

    public AppsaholicEvent setOnEventCompleteListener(OnEventCompleteListener eventCompleteListener) {
        this.eventCompleteListener = eventCompleteListener;
        return this;
    }

    protected String getAdSource() {
        return this.adSource;
    }


    public void setAdSource(String adSource) {
        this.adSource = adSource;
    }
}
