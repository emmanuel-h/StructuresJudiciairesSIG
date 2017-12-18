package com.sig.galherret.structuresjudiciairessig.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sig.galherret.structuresjudiciairessig.R;

import java.util.logging.Logger;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String url = getIntent().getStringExtra("url");
        WebView webView = findViewById(R.id.website);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
