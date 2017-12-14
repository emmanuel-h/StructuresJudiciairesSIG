package com.sig.galherret.structuresjudiciairessig.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.sig.galherret.structuresjudiciairessig.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageToolbar();

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        String content;
        try{
            content = IOUtils.toString(getAssets().open("test.html"),Charset.forName("UTF-8")).replaceAll("%QUI%",getProperty("prenom",getApplicationContext()));
            webView.loadDataWithBaseURL("file:///android_asset/test.html",content,"text/html","UTF-8",null);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void manageToolbar(){
        Button button = findViewById(R.id.buttonShowMore);
        button.setOnClickListener(l -> {
            Toast.makeText(this, "afficher des trucs", Toast.LENGTH_LONG).show();
        });
    }

    private String getProperty(String key,Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("server.properties");
        properties.load(inputStream);
        return properties.getProperty(key);

    }
}
