package com.perk.perksdk.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.perk.perksdk.Base64Images;
import com.perk.perksdk.Utils;
import com.perk.perksdk.appsaholic.PerkManager;

public class LearnMoreActivity extends Activity {

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LearnMoreActivity.this.finish();
        }
    };

    ImageView learnMoreimg,closeme,pointscash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_learn_more_dialog", "layout", getPackageName()));

        TextView tvClose = (TextView) findViewById(getResources().getIdentifier("btn_close", "id", getPackageName()));
        View btnClose = findViewById(getResources().getIdentifier("close_learn_more", "id", getPackageName()));
        View learnMore = findViewById(getResources().getIdentifier("btn_learn_more", "id", getPackageName()));
        learnMoreimg = (ImageView)findViewById(getResources().getIdentifier("btn_learn_more", "id", getPackageName()));
        closeme = (ImageView)findViewById(getResources().getIdentifier("close_learn_more", "id", getPackageName()));
        pointscash = (ImageView)findViewById(getResources().getIdentifier("img_points_to_cash", "id", getPackageName()));

        Drawable learnMoreDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.learn_more_btn));
        Drawable closeDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.close_icon));
        Drawable pointsCashDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.points_cash));

        learnMoreimg.setImageDrawable(learnMoreDrawable);
        closeme.setImageDrawable(closeDrawable);
        pointscash.setImageDrawable(pointsCashDrawable);

        btnClose.setOnClickListener(closeListener);
        tvClose.setOnClickListener(closeListener);
        tvClose.setPaintFlags(tvClose.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.perk.com/appsaholic?apikey=" + PerkManager.getAppKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Utils.m_objContext.startActivity(intent);
            }
        });
    }
}
