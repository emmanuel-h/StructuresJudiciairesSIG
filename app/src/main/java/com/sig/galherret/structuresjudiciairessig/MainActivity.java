package com.sig.galherret.structuresjudiciairessig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.sig.galherret.structuresjudiciairessig.builder.HtmlBuilder;

public class MainActivity extends AppCompatActivity {

    private float posX; // 1.9f
    private float posY; // 47.91f

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageToolbar();

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        HtmlBuilder builder = new HtmlBuilder(this, 1.9f, 47.91f);
        //Logger.getAnonymousLogger().severe("html : " + builder.buildHtml());

        webView.loadData(builder.buildHtml(), "text/html", "UTF-8");
        webView.loadUrl("javascript:showAnnuaireLieuxJustice()");
        //webView.loadUrl("file:///android_asset/mobileTest.html");

    }

    public void manageToolbar(){
        Button button = findViewById(R.id.buttonShowMore);
        button.setOnClickListener(l -> {
            Toast.makeText(this, "afficher des trucs", Toast.LENGTH_LONG).show();
        });
    }
}
