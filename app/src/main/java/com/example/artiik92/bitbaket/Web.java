package com.example.artiik92.bitbaket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by artiik92 on 28.01.2017.
 */

public class Web extends Activity {
    private android.webkit.WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webView = (android.webkit.WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String description = intent.getStringExtra("description");
        if (description != null) {
            webView.loadData(description, "text/html; charset=UTF-8", null);
        } else webView.loadUrl(url);
    }
}
