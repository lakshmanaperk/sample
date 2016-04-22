package com.perk.perksdk.widget;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <h1>CountdownTimer</h1>
 * Widget used between ads to countdown time until the next ad will automatically start.
 * Embed this control within a layout as a custom view to use it.
 */
public class CountdownTimer extends FrameLayout {

    public static final String COUNTDOWN_FINISHED = "com.perk.perksdk.widget.CountdownTimer.FinishedIntent.COUNTDOWN_FINISHED";
    public static final String COUNTDOWN_CANCEL_ALL = "com.perk.perksdk.widget.CountdownTimer.FinishedIntent.COUNTDOWN_CANCEL_ALL";

    public static final int DEFAULT_TIME = 10;
    public static final int MAX_SPINNER_LEVEL = 17;
    private int mDefaultTime;

    private View mView;// need this field in order to make Android Studio's editor happy =]
    private Context mContext;
    private CountDownTimer mTimer;
    private boolean mCancelled;
    private LevelListDrawable llSpinner;
    private TextView tvSecondsRemaining;
    private BroadcastReceiver cancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCancelled = true;
        }
    };

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            mContext.unregisterReceiver(cancelReceiver);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static Intent getFinishedIntent() {
        return new Intent(COUNTDOWN_FINISHED);
    }

    public static Intent getCancelIntent() {
        return new Intent(COUNTDOWN_CANCEL_ALL);
    }

    /**
     * start the internal timer for this control
     */
    public void start(boolean resetview) {
        if(resetview == true) {
            reset();
        }
        mTimer.start();
        mCancelled = false;
    }

    /**
     * stop the internal timer for this control
     */
    public void stop() {
        mTimer.cancel();
    }

    /**
     * reset the timer to its initial state
     */
    public void reset() {
        stop();
        tvSecondsRemaining.setText(String.format("%d", mDefaultTime));
        llSpinner.setLevel(0);
    }

    /**
     * reset the timer to its initial state
     *
     * @param autoplayDuration the server specified duration between ads
     */
    public void reset(@Nullable Integer autoplayDuration, boolean startme) {
        if (autoplayDuration != null) {
            mDefaultTime = autoplayDuration;
        }
        if(startme == true) {
            initTimer(mContext);
            reset();
        }
    }

    /**
     * Constructor included for backwards compatibility. Don't use this.
     *
     * @param context the application context
     */
    @Deprecated
    public CountdownTimer(Context context) {
        super(context);

//        init(context, attrs);
    }

    public CountdownTimer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
        initTimer(context);
    }

    public CountdownTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
        initTimer(context);
    }

    @TargetApi (Build.VERSION_CODES.LOLLIPOP)
    public CountdownTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
        initTimer(context);
    }

    private void initTimer(final Context context) {

        mTimer = new CountDownTimer(mDefaultTime * DateUtils.SECOND_IN_MILLIS, 1000L) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvSecondsRemaining.setText(String.valueOf(millisUntilFinished / DateUtils.SECOND_IN_MILLIS));
                llSpinner.setLevel(llSpinner.getLevel() + 1);
                if (llSpinner.getLevel() > MAX_SPINNER_LEVEL) {
                    llSpinner.setLevel(0);
                }
            }

            @Override
            public void onFinish() {
                if (!mCancelled) {
                    context.sendBroadcast(getFinishedIntent());
                    tvSecondsRemaining.setText("0");
                }
            }
        };
    }

    private void init(Context context, AttributeSet attrs) {

        mContext = context;
        mContext.registerReceiver(cancelReceiver, new IntentFilter(COUNTDOWN_CANCEL_ALL));
        tvSecondsRemaining = new TextView(context);
        llSpinner = new LevelListDrawable();

        if (!isInEditMode()) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = mInflater.inflate(getResources().getIdentifier("countdown_timer", "layout", context.getPackageName()), this);
            ImageView ivCountdown = (ImageView) findViewById(getResources().getIdentifier("countdown_level_list", "id", context.getPackageName()));
            tvSecondsRemaining = (TextView) findViewById(getResources().getIdentifier("seconds_remaining", "id", context.getPackageName()));
            llSpinner = (LevelListDrawable) ivCountdown.getDrawable();
            llSpinner.setLevel(0);
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                new int[]{getResources().getIdentifier("CountdownTimer_time", "styleable", context.getPackageName())},
                0, 0);
        try {
            mDefaultTime = a.getInt(getResources().getIdentifier("CountdownTimer_time", "styleable", context.getPackageName()), DEFAULT_TIME);
        }
        finally {
            a.recycle();
        }

        if (!isInEditMode()) {
            tvSecondsRemaining.setText(String.format("%d", mDefaultTime));
        }
    }
}
