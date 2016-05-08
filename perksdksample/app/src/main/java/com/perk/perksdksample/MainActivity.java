package com.perk.perksdksample;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.perk.perksdk.app.PerkCustomInterface;
import com.perk.perksdk.PerkManager;
import com.perk.perksdk.app.PerkAppInterface;
import com.perk.perksdk.utils.DelayedClickHandler;

import java.io.InputStream;


public class MainActivity extends Activity implements PerkAppInterface {

    public static final String APP_SETTINGS = "App_Settings";
    public static final String TAP_TWICE_EVENT = "ad69fd64248dfc5c163c9fcf688753d4c02eb9c0";
    public static final String TAP_THRICE_EVENT = "7150c12e64a664196e3ffd99832b60b34705d27a";
    public static final String TAP_ONCE_EVENT = "1037c884e25ac9faa84986c38bca8e99ccc07340";

    public static final long HALF_SECOND_DELAY = 500L;
    Dialog customreturndialog, customearningdialog;
    /**
     * SDK Stuff
     */
    CountDownTimer removeViewTimer;
    PerkManagerReceiver receiver;
    /**
     * In App params
     */
    String  tapOnceEvent = "ON", tapTwiceEvent = "ON",
            tapThriceEvent = "ON";
    ToggleButton sdkStatusToggle;
    Button buttonOpenSDK, twiceBtn, onceBtn, thriceBtn, getUserInfo,
            getNotificationCount, claimNotificationPage, dataPointsPage,
            loginStatus, rewards, loadAdButton, loadVideoAdButton, loadDisplayAdButton, publisherbalance,countryList;
    Animation slideLeft, slideRight, flyUp, flyDown;
    TextView logger;
    ImageView profile_image;
    ScrollView logScrollView;
    boolean userinfoclicked = false;
    IntentFilter perkManagerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        perkManagerFilter = new IntentFilter();
        perkManagerFilter.addAction(PerkManager.PERK_MANAGER_NOTIFICATION_STRING);
        perkManagerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PerkManagerReceiver();
        registerReceiver(receiver, perkManagerFilter);
        logger = (TextView) findViewById(R.id.log);
        logScrollView = (ScrollView) findViewById(R.id.log_container);
        logger.setText(getInstanceInfo());
        logger.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                logger.setText("");
                return false;
            }
        });
        PerkManager.startSession(MainActivity.this,MainActivity.this,"81000a9d5407667548eb3ceec9dc699823a02ba9");
        buttonOpenSDK = (Button) findViewById(R.id.buttonOpenSDK);
        onceBtn = (Button) findViewById(R.id.onceBtn);
        twiceBtn = (Button) findViewById(R.id.twiceBtn);
        thriceBtn = (Button) findViewById(R.id.thriceBtn);
        getUserInfo = (Button) findViewById(R.id.getUserPoints);
        getNotificationCount = (Button) findViewById(R.id.getNotificationCount);
        claimNotificationPage = (Button) findViewById(R.id.claimNotificationPage);
        dataPointsPage = (Button) findViewById(R.id.launchDatapoints);
        loginStatus = (Button) findViewById(R.id.loginstatus);
        rewards = (Button) findViewById(R.id.rewards);
        countryList = (Button)findViewById(R.id.countryList);
        loadAdButton = (Button) findViewById(R.id.loadAd);
        loadVideoAdButton = (Button) findViewById(R.id.loadVideoAd);
        loadDisplayAdButton = (Button) findViewById(R.id.loadDisplayAd);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        publisherbalance = (Button) findViewById(R.id.publisherbalance);
        buttonOpenSDK.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showPortal(MainActivity.this);
            }
        });

        onceBtn.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                if (tapOnceEvent.equals("ON")) {
                    PerkManager.trackEvent(MainActivity.this,
                            TAP_ONCE_EVENT, false, createCustomInterfaceWithId(TAP_ONCE_EVENT));

                }
                else {
                    Toast.makeText(getApplicationContext(), "Setting is Off!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        getUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userinfoclicked = true;
                if (PerkManager.getPerkUserInfo(getApplicationContext()) == false) {
                    Toast.makeText(getApplicationContext(), "Login PERK to see Points",
                            Toast.LENGTH_SHORT).show();
                    userinfoclicked = false;
                }
            }
        });
        twiceBtn.setOnClickListener(new DelayedClickHandler(HALF_SECOND_DELAY) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                if (tapTwiceEvent.equals("ON")) {
                    PerkManager.trackEvent(MainActivity.this,
                            TAP_TWICE_EVENT, true, createCustomInterfaceWithId(TAP_TWICE_EVENT));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Setting is Off!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        thriceBtn.setOnClickListener(new DelayedClickHandler(HALF_SECOND_DELAY) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                if (tapThriceEvent.equals("ON")) {
                    PerkManager.trackEvent(MainActivity.this,
                            TAP_THRICE_EVENT, false, createCustomInterfaceWithId(TAP_THRICE_EVENT));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Setting is Off!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        countryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.getCountriesList(MainActivity.this);
            }
        });

        getNotificationCount.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.fetchNotificationsCount(MainActivity.this);
            }
        });

        claimNotificationPage.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.claimNotificationPage(MainActivity.this);
            }
        });

        dataPointsPage.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.launchSurvey(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7");
            }
        });



        loginStatus.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                if (PerkManager.isPerkUserLoggedIn()) {
                    PerkManager.logoutUser(MainActivity.this);
                    loginStatus.setText("Login User");
                }
                else {
                    PerkManager.launchLoginPage(MainActivity.this);
                }
            }
        });

        rewards.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.showPrizePage(getApplicationContext());
            }
        });

        loadAdButton.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showAd(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7");
            }
        });

        loadVideoAdButton.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showVideoAd(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7");
            }
        });

        loadDisplayAdButton.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showStaticAd(MainActivity.this, "f0c902bd33a74e7d6504696dffedc66e5dbdb47c");
            }
        });

        sdkStatusToggle = (ToggleButton) findViewById(R.id.checkUserToggleStatus);
        sdkStatusToggle.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.togglePerkSdkStatus(MainActivity.this);
            }
        });
        publisherbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.getPublisherAvailablePoints(MainActivity.this);
            }
        });
    }

    private String getInstanceInfo() {
        String out = "";
        out += Build.BRAND + " " + Build.DEVICE + "\n";
        return out;
    }

    public static void fadeOutAndHideImage(final RelativeLayout rlp) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                rlp.setVisibility(View.INVISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        rlp.startAnimation(fadeOut);
    }

    public class PerkManagerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String thestr = arg1.getStringExtra("notification");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PerkManager.isPerkUserLoggedIn() == true) {
            loginStatus.setText("Logout user");
            if (sdkStatusToggle.isEnabled()) {
                PerkManager.getPerkUserInfo(getApplicationContext());
            }
        }
        else {
            loginStatus.setText("Login user");
        }
        sdkStatusToggle.setChecked(PerkManager.getPerkSDKStatus());
    }

    @Override
    protected void onDestroy() {
        Log.w("Perk Main", "onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public void onInit(boolean statusCode, String statusMessage) {
        sdkStatusToggle.setChecked(statusCode);
        sdkStatusToggle.setEnabled(true);
        loginStatus.setEnabled(true);
        if (PerkManager.isPerkUserLoggedIn() == true) {
            loginStatus.setText("Logout user");
            PerkManager.getPerkUserInfo(getApplicationContext());
        }
        else {
            loginStatus.setText("Login user");
        }
        logger.append(statusMessage + "\n");
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onNotifiationsCount(boolean statusCode, int unreadNotification) {
        if(statusCode == true) {
            Toast.makeText(
                    getApplicationContext(),
                    "You have " + unreadNotification
                            + " notification pending",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // API may failed to get latest , show recently updated and stored locally (optional)
            Toast.makeText(
                    getApplicationContext(),
                    "You have " + PerkManager.getUnreadNotificationsCount(MainActivity.this)
                            + " notification pending",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSdkStatus(boolean statusCode, boolean sdkStatus) {
        sdkStatusToggle.setChecked(sdkStatus);
        logger.append("sdk status is " + sdkStatus + "\n");
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onUserInformation(boolean statusCode) {
        if (statusCode == true) {
            try {
                String userId = PerkManager.getPerkUserId();
                String userEmail = PerkManager.getPerkUserEmail();
                String userFirstName = PerkManager.getPerkUserFirstName();
                String userLastName = PerkManager.getPerkUserLastName();
                String profileImage = PerkManager.getPerkUserProfileImageUrl();
                int userAvailablePoints = PerkManager.getPerkUserAvailablePoints();
                int userPendingPoints = PerkManager.getPerkUserPendingPoints();

                if (userinfoclicked == true) {
                    Toast.makeText(
                            getApplicationContext(),
                            "User Has Total " + userAvailablePoints
                                    + " Perk Points",
                            Toast.LENGTH_SHORT).show();
                    userinfoclicked = false;
                }
                if (profileImage.length() > 0) {
                    new DownloadImageTask(profile_image).execute(profileImage);
                }
            }catch(Exception e){

            }
        }
        else {
            Toast.makeText(
                    getApplicationContext(),
                    "User Has Total " + PerkManager.getPerkUserAvailablePoints()
                            + " Perk Points",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCountryList(boolean statusCode,String countryList) {
        if(statusCode == true) {
            if (countryList.length() > 0) {
                Toast.makeText(
                        getApplicationContext(),
                        "Available Countries " + countryList,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else
        {

        }
    }

    @Override
    public void onPublisherBalance(boolean statusCode, int prepaidPoints) {
        if (statusCode == true) {
            Toast.makeText(
                    getApplicationContext(),
                    "Publisher Has Total " + prepaidPoints
                            + " Perk Points",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(
                    getApplicationContext(),
                    "User Has Total " + prepaidPoints
                            + " Perk Points",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPerkEvent(String message) {
        logger.append(message + "\n");
        logScrollView.fullScroll(View.FOCUS_DOWN);
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT);

        //todo:replace hacky event handling with real events including code and message
        switch (message) {
            case "claimNotificationClosed":
                // do Your Stuff here
                break;
            default:
                break;

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }
            catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setVisibility(View.VISIBLE);
            bmImage.setImageBitmap(result);
        }
    }





    private PerkCustomInterface createCustomInterfaceWithId(final String eventId) {
        return new PerkCustomInterface() {

            @Override
            public void showEarningDialog() {

                //Verify that this isn't zero.
                if (PerkManager.getAchievementPoints() == 0) {
                    return;
                }

                if (customearningdialog != null && customearningdialog.isShowing()) {
                    customearningdialog.dismiss();
                }

                slideRight = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_right);
                slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_left);
                flyUp = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                flyDown = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);

                String offset = "", animation = "", backgroundColor = "", fontColor = "", btnColor = "", btnTextColor = "", closeButtonCheck = "ON";//todo:wat...
                int timeDelay = 0;

                SharedPreferences prefs = getSharedPreferences(APP_SETTINGS, 0);
                offset = prefs.getString("offset", "");
                animation = prefs.getString("animation", "");
                backgroundColor = prefs.getString("bgColor", "");
                fontColor = prefs.getString("fontColor", "");
                btnColor = prefs.getString("btnColor", "");
                btnTextColor = prefs.getString("btnTextColor", "");
                closeButtonCheck = prefs.getString("closeButtonCheck", "ON");
                timeDelay = prefs.getInt("timeDelay", 0);

                if (backgroundColor.equals("")) {
                    backgroundColor = "#333333";
                }

                if (fontColor.equals("")) {
                    fontColor = "#ffffff";
                }

                if (btnColor.equals("")) {
                    btnColor = "#69d06e";
                }

                if (btnTextColor.equals("")) {
                    btnTextColor = "#ffffff";
                }

                if (timeDelay == 0) {
                    timeDelay = 2;
                }

                final RelativeLayout notificationTextLayout, claimButtonLayout;
                TextView notificationText, earnedPoints, earnedPointsBanner;
                Button claimButton;
                ImageButton closeButton;


                customearningdialog = new Dialog(MainActivity.this);
                customearningdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customearningdialog.setContentView(R.layout.event_notification);

                notificationTextLayout = (RelativeLayout) customearningdialog.findViewById(R.id.notificationTextLayout);
                claimButtonLayout = (RelativeLayout) customearningdialog.findViewById(R.id.claimButtonLayout);
                notificationText = (TextView) customearningdialog.findViewById(R.id.notificationText);
                earnedPoints = (TextView) customearningdialog.findViewById(R.id.earnedPoints);
                earnedPointsBanner = (TextView) customearningdialog.findViewById(R.id.earnedPointsBanner);
                claimButton = (Button) customearningdialog.findViewById(R.id.claimButton);
                closeButton = (ImageButton) customearningdialog.findViewById(R.id.closeButton);

                if (closeButtonCheck.equals("ON")) {
                    customearningdialog.setCanceledOnTouchOutside(false);
                }
                else {
                    customearningdialog.setCanceledOnTouchOutside(true);
                    closeButton.setVisibility(View.INVISIBLE);
                }

                notificationTextLayout.setBackgroundColor(Color
                        .parseColor(backgroundColor));
                claimButtonLayout.setBackgroundColor(Color.parseColor(backgroundColor));

                notificationText.setTextColor(Color.parseColor(fontColor));
                earnedPoints.setTextColor(Color.parseColor(fontColor));
                earnedPointsBanner.setTextColor(Color.parseColor(fontColor));
                claimButton.setBackgroundColor(Color.parseColor(btnColor));
                claimButton.setTextColor(Color.parseColor(btnTextColor));

                customearningdialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

                int eventPoint = PerkManager.getAchievementPoints();

                if (eventPoint > 1) {
                    earnedPointsBanner.setText("Perk Points!");
                    claimButton.setText("Claim");
                }
                else {
                    earnedPointsBanner.setText("Perk Point!");
                    claimButton.setText("Claim");
                }

                notificationText.setText("You've earned " + PerkManager.getAchievementPoints() + " Points " + PerkManager.getAchievementNotiticationText());
                earnedPoints.setText("+" + PerkManager.getAchievementPoints());

                closeButton.setOnClickListener(new DelayedClickHandler() {
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);

                        customearningdialog.dismiss();
                    }
                });


                ImageButton btnInfo = (ImageButton) customearningdialog.findViewById(R.id.btnInfo);
                btnInfo.setOnClickListener(new DelayedClickHandler() {

                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                    }
                });


                Window window = customearningdialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.copyFrom(window.getAttributes());
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                // Setting up offset according to the custom settings
                if (offset.equals("Top")) {
                    wlp.gravity = Gravity.TOP;
                }
                else if (offset.equals("Center")) {
                    wlp.gravity = Gravity.CENTER_VERTICAL;
                }
                else {
                    wlp.gravity = Gravity.BOTTOM;
                }

                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                claimButton.setOnClickListener(new DelayedClickHandler() {
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);

                        PerkManager.claimPoints(MainActivity.this);
                        customearningdialog.dismiss();
                    }
                });

                customearningdialog.show();

                if (animation.equals("Slide Left")) {
                    notificationTextLayout.setAnimation(slideLeft);
                }
                else if (animation.equals("Slide Right")) {
                    notificationTextLayout.setAnimation(slideRight);
                }
                else if (animation.equals("Fly Up")) {
                    notificationTextLayout.setAnimation(flyUp);
                }
                else if (animation.equals("Fly Down")) {
                    notificationTextLayout.setAnimation(flyDown);
                }
                else {
                    notificationTextLayout.setAnimation(null);
                }

                removeViewTimer = new CountDownTimer(timeDelay * 5000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        fadeOutAndHideImage(notificationTextLayout);
                    }

                }.start();

            }

            @Override
            public void showReturnNotfication() {


                if (customreturndialog != null && customreturndialog.isShowing()) {
                    customreturndialog.dismiss();
                }

                slideRight = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_right);
                slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_left);
                flyUp = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                flyDown = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);

                String offset = "", animation = "", backgroundColor = "", fontColor = "", btnColor = "", btnTextColor = "", closeButtonCheck = "ON";//todo:wat...
                int timeDelay = 0;

                SharedPreferences prefs = getSharedPreferences(APP_SETTINGS, 0);
                offset = prefs.getString("offset", "");
                animation = prefs.getString("animation", "");
                backgroundColor = prefs.getString("bgColor", "");
                fontColor = prefs.getString("fontColor", "");
                btnColor = prefs.getString("btnColor", "");
                btnTextColor = prefs.getString("btnTextColor", "");
                closeButtonCheck = prefs.getString("closeButtonCheck", "ON");
                timeDelay = prefs.getInt("timeDelay", 0);

                if (backgroundColor.equals("")) {
                    backgroundColor = "#333333";
                }

                if (fontColor.equals("")) {
                    fontColor = "#ffffff";
                }

                if (btnColor.equals("")) {
                    btnColor = "#69d06e";
                }

                if (btnTextColor.equals("")) {
                    btnTextColor = "#ffffff";
                }

                if (timeDelay == 0) {
                    timeDelay = 2;
                }

                final RelativeLayout notificationTextLayout, claimButtonLayout;
                TextView notificationText, earnedPoints, earnedPointsBanner;
                ImageButton closeButton;
                Button claimButton;


                customreturndialog = new Dialog(MainActivity.this);
                customreturndialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customreturndialog.setContentView(R.layout.event_notification);

                notificationTextLayout = (RelativeLayout) customreturndialog.findViewById(R.id.notificationTextLayout);
                claimButtonLayout = (RelativeLayout) customreturndialog.findViewById(R.id.claimButtonLayout);
                notificationText = (TextView) customreturndialog.findViewById(R.id.notificationText);
                earnedPoints = (TextView) customreturndialog.findViewById(R.id.earnedPoints);
                earnedPointsBanner = (TextView) customreturndialog.findViewById(R.id.earnedPointsBanner);
                claimButton = (Button) customreturndialog.findViewById(R.id.claimButton);
                closeButton = (ImageButton) customreturndialog.findViewById(R.id.closeButton);

                if (closeButtonCheck.equals("ON")) {
                    customreturndialog.setCanceledOnTouchOutside(false);
                }
                else {
                    customreturndialog.setCanceledOnTouchOutside(true);
                    closeButton.setVisibility(View.INVISIBLE);
                }

                notificationTextLayout.setBackgroundColor(Color
                        .parseColor(backgroundColor));
                claimButtonLayout.setBackgroundColor(Color.parseColor(backgroundColor));

                notificationText.setTextColor(Color.parseColor(fontColor));
                earnedPoints.setTextColor(Color.parseColor(fontColor));
                earnedPointsBanner.setTextColor(Color.parseColor(fontColor));
                claimButton.setVisibility(View.GONE);
                customreturndialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

                int totalearnedpoints = PerkManager.getAchievementPoints();

                if (totalearnedpoints > 1) {
                    earnedPointsBanner.setText("Perk Points!");
                }
                else {
                    earnedPointsBanner.setText("Perk Point!");
                }

                notificationText.setText(" Congrats" + "\n" + "You've earned " + totalearnedpoints + " Points " + PerkManager.getAchievementNotiticationText());
                earnedPoints.setText("+" + totalearnedpoints);

                closeButton.setOnClickListener(new DelayedClickHandler() {
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);

                        customreturndialog.dismiss();
                    }
                });


                ImageButton btnInfo = (ImageButton) customreturndialog.findViewById(R.id.btnInfo);
                btnInfo.setOnClickListener(new DelayedClickHandler() {

                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                    }

                });


                Window window = customreturndialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.copyFrom(window.getAttributes());
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                // Setting up offset according to the custom settings
                if (offset.equals("Top")) {
                    wlp.gravity = Gravity.TOP;
                }
                else if (offset.equals("Center")) {
                    wlp.gravity = Gravity.CENTER_VERTICAL;
                }
                else {
                    wlp.gravity = Gravity.BOTTOM;
                }

                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);


                customreturndialog.show();

                if (animation.equals("Slide Left")) {
                    notificationTextLayout.setAnimation(slideLeft);
                }
                else if (animation.equals("Slide Right")) {
                    notificationTextLayout.setAnimation(slideRight);
                }
                else if (animation.equals("Fly Up")) {
                    notificationTextLayout.setAnimation(flyUp);
                }
                else if (animation.equals("Fly Down")) {
                    notificationTextLayout.setAnimation(flyDown);
                }
                else {
                    notificationTextLayout.setAnimation(null);
                }

                removeViewTimer = new CountDownTimer(timeDelay * 5000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        fadeOutAndHideImage(notificationTextLayout);
                    }

                }.start();
            }

        };
    }
}

