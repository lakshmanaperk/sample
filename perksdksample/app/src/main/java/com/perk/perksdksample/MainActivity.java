package com.perk.perksdksample;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.perk.perksdk.PerkManager;
import com.perk.perksdk.app.PerkAppInterface;
import com.perk.perksdk.app.PerkCustomInterface;
import com.perk.perksdk.sdkconfig.PerkUserInfo;
import com.perk.perksdk.utils.DelayedClickHandler;

import java.io.InputStream;


public class MainActivity extends Activity implements PerkAppInterface {

    public static final String APP_SETTINGS = "App_Settings";
    public static final String TAP_TWICE_EVENT = "ad69fd64248dfc5c163c9fcf688753d4c02eb9c0";
    public static final String TAP_THRICE_EVENT = "7150c12e64a664196e3ffd99832b60b34705d27a";
    public static final String TAP_ONCE_EVENT = "1037c884e25ac9faa84986c38bca8e99ccc07340";

    public static final long HALF_SECOND_DELAY = 500L;
    Dialog customReturnDialog, customEarningDialog;
    /**
     * SDK Stuff
     */
    CountDownTimer removeViewTimer;
    PerkManagerReceiver receiver;
    View.DragShadowBuilder shadowBuilder;
    /**
     * In App params
     */
    Button tapTwiceBtn, tapOnceBtn, tapThriceBtn,portalPage, unclaimedPage,
            rewardsPage,unclaimedCount,supportedCountries,publisherBalance,showAds,showTwoAds,
            showThreeAdsWithLoader,showSurvey,loginUser,logoutUser;

    Animation slideLeft, slideRight, flyUp, flyDown;

    TextView userName,userEmail,userPoints,userPendingPoints,userPerkId,logger,sdkStatusText;

    IntentFilter perkManagerFilter;

    LinearLayout loggedInLayout , loggedOutLayout,borderView,main,topLayout;
    RelativeLayout bottomLayout;
    ScrollView logScrollView;
    Switch sdkStatusSwitch;

    float init_y = 0,b_y,last_b_y,bheight,initheight,sheight;
    ImageView userProfileImage,userStatusIn, userStatusOut;
    DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        perkManagerFilter = new IntentFilter();
        perkManagerFilter.addAction(PerkManager.PERK_MANAGER_NOTIFICATION_STRING);
        perkManagerFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PerkManagerReceiver();
        registerReceiver(receiver, perkManagerFilter);
        logger = (TextView) findViewById(R.id.sdk_event_log);
        logScrollView = (ScrollView) findViewById(R.id.log_container);
        logger.setText(getInstanceInfo());
//        logger.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                logger.setText("");
//                return false;
//            }
//        });
        displayMetrics = new DisplayMetrics();
        (MainActivity.this).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);

        PerkManager.startSession(MainActivity.this,MainActivity.this,"81000a9d5407667548eb3ceec9dc699823a02ba9");

        loggedInLayout = (LinearLayout)findViewById(R.id.loggedin_layout);
        loggedOutLayout = (LinearLayout)findViewById(R.id.loggedout_layout);
        topLayout = (LinearLayout) findViewById(R.id.top_layout);
        bottomLayout = (RelativeLayout)findViewById(R.id.bottom_layout);
        borderView = (LinearLayout)findViewById(R.id.border);

        LinearLayout.LayoutParams topParams =  new LinearLayout.LayoutParams(((int)getWidthtForView(1)),(int)(getHeightForView((float)0.84)));
        LinearLayout.LayoutParams bottomParams =  new LinearLayout.LayoutParams(((int)getWidthtForView(1)),(int)(getHeightForView((float)0.14)));
        RelativeLayout.LayoutParams borderParams =  new RelativeLayout.LayoutParams(((int)getWidthtForView(1)),(int)(getHeightForView((float)0.02)));

        topLayout.setLayoutParams(topParams);
        bottomLayout.setLayoutParams(bottomParams);
        borderView.setLayoutParams(borderParams);



        loggedInLayout.setVisibility(View.GONE);
        loggedOutLayout.setVisibility(View.VISIBLE);
        sdkStatusSwitch = (Switch)findViewById(R.id.sdk_status_switch);
        sdkStatusSwitch.setChecked(false);

        tapOnceBtn = (Button) findViewById(R.id.tap_once);
        tapTwiceBtn = (Button) findViewById(R.id.tap_twice);
        tapThriceBtn = (Button) findViewById(R.id.tap_thrice);

        portalPage = (Button)findViewById(R.id.portal_page);
        unclaimedPage = (Button)findViewById(R.id.unclaimed_page);
        rewardsPage = (Button)findViewById(R.id.rewards_page);

        unclaimedCount = (Button)findViewById(R.id.unclaimed_count);
        supportedCountries = (Button)findViewById(R.id.supported_countries);
        publisherBalance = (Button)findViewById(R.id.publisher_balance);
        userStatusIn = (ImageView)findViewById(R.id.user_status_in);
        userStatusOut = (ImageView)findViewById(R.id.user_status_out);

        showAds = (Button)findViewById(R.id.show_ad);
        showTwoAds = (Button)findViewById(R.id.show_two_ads);
        showThreeAdsWithLoader = (Button)findViewById(R.id.show_three_ads_load);
        showSurvey = (Button)findViewById(R.id.show_survey);

        logoutUser = (Button)findViewById(R.id.user_status_loggedin);
        loginUser = (Button)findViewById(R.id.user_status_logggedout);

        sdkStatusText = (TextView)findViewById(R.id.sdk_status_text);

        userProfileImage = (ImageView)findViewById(R.id.user_image);

        userEmail = (TextView)findViewById(R.id.user_email);
        userName = (TextView)findViewById(R.id.user_name);
        userPoints = (TextView)findViewById(R.id.user_points);
        userPendingPoints = (TextView)findViewById(R.id.user_ppoints);
        userPerkId = (TextView)findViewById(R.id.user_id);
        sdkStatusSwitch.setEnabled(false);


        main = (LinearLayout)findViewById(R.id.main);
        borderView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bheight = borderView.getLayoutParams().height;
                ClipData data = ClipData.newPlainText("", "");
                if(init_y <= 0)
                    init_y = bottomLayout.getY();
                shadowBuilder = new View.DragShadowBuilder(borderView);
                borderView.startDrag(data, shadowBuilder, borderView, 0);
                borderView.setBackgroundColor(Color.parseColor("#00FF00"));
                return true;
            }
        });



        main.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        last_b_y = event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        b_y = event.getY();
                        if(init_y < b_y) {
                            return true;
                        }
                        float diff = (last_b_y - b_y );
                        sheight =  sheight + diff;
                        bottomLayout.getLayoutParams().height = (int)sheight;
                        bottomLayout.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        bottomLayout.setY(b_y);
                        bottomLayout.bringToFront();
                        main.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:

                        if(init_y < b_y) {
                            bottomLayout.setY(init_y);
                        }
                        bottomLayout.bringToFront();
                        main.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        if(init_y < b_y) {
                            bottomLayout.setY(init_y);
                        }
                        bottomLayout.bringToFront();
                        borderView.setBackgroundColor(Color.parseColor("#87CEFA"));
                        main.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if(init_y < b_y) {
                            bottomLayout.setY(init_y);
                        }
                        bottomLayout.bringToFront();
                        borderView.setBackgroundColor(Color.parseColor("#87CEFA"));
                        main.invalidate();
                        break;
                    default:
                        if(init_y < b_y) {
                            bottomLayout.setY(init_y);
                        }
                        bottomLayout.bringToFront();
                        borderView.setBackgroundColor(Color.parseColor("#87CEFA"));
                        main.invalidate();
                        break;
                }
                return true;
            }
        });

        portalPage.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.showPortal(MainActivity.this,"portal");
            }
        });

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.showPortal(MainActivity.this,"login");
            }
        });

        userStatusIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PerkManager.isPerkUserLoggedIn()) {
                    Toast.makeText(
                            getApplicationContext(), "User is Logged in",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(), "User is Logged out",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        userStatusOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PerkManager.isPerkUserLoggedIn()) {
                    Toast.makeText(
                            getApplicationContext(), "User is Logged in",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(), "User is Logged out",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.logoutUser(MainActivity.this);
                loggedInLayout.setVisibility(View.GONE);
                loggedOutLayout.setVisibility(View.VISIBLE);
            }
        });
        tapOnceBtn.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.trackEvent(MainActivity.this,
                        TAP_ONCE_EVENT, false, createCustomInterfaceWithId(TAP_ONCE_EVENT));

            }
        });
        tapTwiceBtn.setOnClickListener(new DelayedClickHandler(HALF_SECOND_DELAY) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.trackEvent(MainActivity.this,
                        TAP_TWICE_EVENT, true, createCustomInterfaceWithId(TAP_TWICE_EVENT));
            }
        });

        tapThriceBtn.setOnClickListener(new DelayedClickHandler(HALF_SECOND_DELAY) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.trackEvent(MainActivity.this,
                        TAP_THRICE_EVENT, false, createCustomInterfaceWithId(TAP_THRICE_EVENT));
            }
        });

        supportedCountries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.fetch(MainActivity.this,"supportedCountries");
            }
        });

        unclaimedCount.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.fetch(MainActivity.this,"notifications");
            }
        });

        unclaimedPage.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.showPortal(MainActivity.this,"unclaimed");
            }
        });

        showSurvey.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.launchSurvey(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7");
            }
        });



        rewardsPage.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PerkManager.showPortal(getApplicationContext(),"rewards");
            }
        });

        showAds.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showAds(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7",1,false);
            }
        });

        showTwoAds.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showAds(MainActivity.this, "05536119cdbdf1c7baff4c0427a467c5a1745ff7",2,false);
            }
        });

        showThreeAdsWithLoader.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PerkManager.showAds(MainActivity.this, "f0c902bd33a74e7d6504696dffedc66e5dbdb47c",3,true);
            }
        });

        sdkStatusSwitch.setOnClickListener(new DelayedClickHandler() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                sdkStatusSwitch.setChecked(PerkManager.getPerkSDKStatus());
                PerkManager.togglePerkSdkStatus(MainActivity.this);
                sdkStatusSwitch.setEnabled(false);

            }
        });
        publisherBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.fetch(MainActivity.this,"publisherPointBalance");
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public  float  getHeightForView(float screenShare) {
        return displayMetrics.heightPixels * screenShare;
    }
    public  float  getWidthtForView(float screenShare) {
        return displayMetrics.widthPixels * screenShare;
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

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
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
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initheight = bottomLayout.getLayoutParams().height;
        bottomLayout.setMinimumHeight((int)initheight);
        sdkStatusSwitch.setChecked(PerkManager.getPerkSDKStatus());
        if (PerkManager.isPerkUserLoggedIn() == true) {
            if (sdkStatusSwitch.isEnabled()) {
                PerkManager.fetch(getApplicationContext(),"userInfo");
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.w("Perk Main", "onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public void onInit(boolean statusCode, String statusMessage) {
        sdkStatusSwitch.setChecked(statusCode);
        sdkStatusSwitch.setEnabled(true);
        sdkStatusText.setText("SDK Enabled");
        if (PerkManager.isPerkUserLoggedIn() == true) {
            loggedInLayout.setVisibility(View.VISIBLE);
            loggedOutLayout.setVisibility(View.GONE);
            PerkManager.fetch(getApplicationContext(),"userInfo");
        }
        else {
            loggedOutLayout.setVisibility(View.VISIBLE);
            loggedInLayout.setVisibility(View.GONE);
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
        sdkStatusSwitch.setEnabled(true);
        sdkStatusSwitch.setChecked(sdkStatus);

        if(sdkStatus) {
            sdkStatusText.setText("SDK Enabled");
        }
        else {
            sdkStatusText.setText("SDK Disabled");
        }
        logger.append("sdk status is " + sdkStatus + "\n");
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onUserInformation(boolean statusCode, PerkUserInfo info) {
        if (statusCode == true) {
            try {

                loggedInLayout.setVisibility(View.VISIBLE);
                loggedOutLayout.setVisibility(View.GONE);

                userEmail.setText(info.getUserEmail());
                userName.setText(info.getUserFirstName() + " " + info.getUserLastName());
                String profileImage = info.getUserProfileImageUrl();
                userPoints.setText("Points : " + info.getUserAvailablePoints());
                userPendingPoints.setText("Pending Points : " + info.getUserPendingPoints());
                userPerkId.setText("perkID : " + info.getUserId());

                if (profileImage.length() > 0) {
                    new DownloadImageTask(userProfileImage).execute(profileImage);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
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

                if (customEarningDialog != null && customEarningDialog.isShowing()) {
                    customEarningDialog.dismiss();
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


                customEarningDialog = new Dialog(MainActivity.this);
                customEarningDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customEarningDialog.setContentView(R.layout.event_notification);

                notificationTextLayout = (RelativeLayout) customEarningDialog.findViewById(R.id.notificationTextLayout);
                claimButtonLayout = (RelativeLayout) customEarningDialog.findViewById(R.id.claimButtonLayout);
                notificationText = (TextView) customEarningDialog.findViewById(R.id.notificationText);
                earnedPoints = (TextView) customEarningDialog.findViewById(R.id.earnedPoints);
                earnedPointsBanner = (TextView) customEarningDialog.findViewById(R.id.earnedPointsBanner);
                claimButton = (Button) customEarningDialog.findViewById(R.id.claimButton);
                closeButton = (ImageButton) customEarningDialog.findViewById(R.id.closeButton);

                if (closeButtonCheck.equals("ON")) {
                    customEarningDialog.setCanceledOnTouchOutside(false);
                }
                else {
                    customEarningDialog.setCanceledOnTouchOutside(true);
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

                customEarningDialog.getWindow().setBackgroundDrawable(
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

                        customEarningDialog.dismiss();
                    }
                });


                ImageButton btnInfo = (ImageButton) customEarningDialog.findViewById(R.id.btnInfo);
                btnInfo.setOnClickListener(new DelayedClickHandler() {

                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                    }
                });


                Window window = customEarningDialog.getWindow();
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

                        PerkManager.claimEvent(MainActivity.this);
                        customEarningDialog.dismiss();
                    }
                });

                customEarningDialog.show();

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
            public void showReturnNotification() {


                if (customReturnDialog != null && customReturnDialog.isShowing()) {
                    customReturnDialog.dismiss();
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


                customReturnDialog = new Dialog(MainActivity.this);
                customReturnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customReturnDialog.setContentView(R.layout.event_notification);

                notificationTextLayout = (RelativeLayout) customReturnDialog.findViewById(R.id.notificationTextLayout);
                claimButtonLayout = (RelativeLayout) customReturnDialog.findViewById(R.id.claimButtonLayout);
                notificationText = (TextView) customReturnDialog.findViewById(R.id.notificationText);
                earnedPoints = (TextView) customReturnDialog.findViewById(R.id.earnedPoints);
                earnedPointsBanner = (TextView) customReturnDialog.findViewById(R.id.earnedPointsBanner);
                claimButton = (Button) customReturnDialog.findViewById(R.id.claimButton);
                closeButton = (ImageButton) customReturnDialog.findViewById(R.id.closeButton);

                if (closeButtonCheck.equals("ON")) {
                    customReturnDialog.setCanceledOnTouchOutside(false);
                }
                else {
                    customReturnDialog.setCanceledOnTouchOutside(true);
                    closeButton.setVisibility(View.INVISIBLE);
                }

                notificationTextLayout.setBackgroundColor(Color
                        .parseColor(backgroundColor));
                claimButtonLayout.setBackgroundColor(Color.parseColor(backgroundColor));

                notificationText.setTextColor(Color.parseColor(fontColor));
                earnedPoints.setTextColor(Color.parseColor(fontColor));
                earnedPointsBanner.setTextColor(Color.parseColor(fontColor));
                claimButton.setVisibility(View.GONE);
                customReturnDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

                int totalearnedpoints = PerkManager.getEventTotalPoints();

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

                        customReturnDialog.dismiss();
                    }
                });


                ImageButton btnInfo = (ImageButton) customReturnDialog.findViewById(R.id.btnInfo);
                btnInfo.setOnClickListener(new DelayedClickHandler() {

                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                    }

                });


                Window window = customReturnDialog.getWindow();
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


                customReturnDialog.show();

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

