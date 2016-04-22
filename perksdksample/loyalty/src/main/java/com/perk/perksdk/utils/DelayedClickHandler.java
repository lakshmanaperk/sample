package com.perk.perksdk.utils;

import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Prevent multiple clicks on interactive UI elements
 */
public class DelayedClickHandler implements OnClickListener {

    private static final long DEFAULT_TIME = DateUtils.SECOND_IN_MILLIS * 3;
    private final long mDelay;

    public DelayedClickHandler() {
        mDelay = DEFAULT_TIME;
    }

    public DelayedClickHandler(long time) {
        mDelay = (time > 0) ? time : DEFAULT_TIME;
    }

    @Override
    public void onClick(final View v) {
        v.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setClickable(true);
            }
        }, mDelay);
    }
}
