package com.sig.galherret.structuresjudiciairessig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        //webView.loadUrl("http://openlayers.org/en/latest/build/ol.js");
        //webView.loadUrl("http://192.168.1.38:8080/openlayers/v4/ol.js");
        webView.loadUrl("http://192.168.1.38:8080/structuresJudiciaires/mobileTest.html");

        //webView.loadUrl("http://openlayers.org/en/latest/examples/mobile-full-screen.html?q=mobile");

    }
}
