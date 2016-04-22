package com.perk.perksdk.appsaholic.web;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <h1>PortalView</h1>
 */
public class PortalView extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_portal_view", "layout", getPackageName()));
        WebView webview = (WebView) findViewById(getResources().getIdentifier("web_view", "id", getPackageName()));
        String url = getIntent().getData().toString();

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                switch (url) {
                    case "nofill://datapoints":
                        return true;
                }

                return false;
            }
        });

        webview.loadUrl(url);

    }
}
