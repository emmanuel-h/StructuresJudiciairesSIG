package com.sig.galherret.structuresjudiciairessig;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.sig.galherret.structuresjudiciairessig.builder.HtmlBuilder;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private float posX; // 1.9f
    private float posY; // 47.91f

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        HtmlBuilder builder = new HtmlBuilder(this, 1.9f, 47.91f);
        //Logger.getAnonymousLogger().severe("html : " + builder.buildHtml());

        webView.loadData(builder.buildHtml(), "text/html", "UTF-8");
        webView.loadUrl("javascript:showAnnuaireLieuxJustice()");
        //webView.loadUrl("file:///android_asset/mobileTest.html");

    }
}
