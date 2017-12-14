package com.sig.galherret.structuresjudiciairessig.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.sig.galherret.structuresjudiciairessig.R;
import com.sig.galherret.structuresjudiciairessig.model.GPSService;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private float longitude;
    private float latitude;
    private final float DEFAULT_LATITUDE = 48.859489f;
    private final float DEFAULT_LONGITUDE = 2.320582f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // We ask the user to access the location
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this,permissions,42);

        manageToolbar();

        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
        loadFile();
    }

    private void loadFile(){
        getPrefs();
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String content;
        try{
            content = IOUtils.toString(getAssets().open("mobileTest.html"),Charset.forName("UTF-8"))
                    .replaceAll("%LATITUDE%", String.valueOf(latitude))
                    .replaceAll("%LONGITUDE%", String.valueOf(longitude));
            webView.loadDataWithBaseURL("file:///android_asset/mobileTest.html", content, "text/html", "UTF-8", null);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void manageToolbar(){
        Button button = findViewById(R.id.buttonShowMore);
        button.setOnClickListener(l -> loadFile());
    }

    private String getServerProperties(String key,Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("server.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    private void getPrefs(){
        SharedPreferences userPrefs = getSharedPreferences("coordinates", MODE_PRIVATE);
        // If there is no known position, we center the map on Paris
        latitude = userPrefs.getFloat("lastKnownLatitude",DEFAULT_LATITUDE);
        longitude = userPrefs.getFloat("lastKnownLongitude",DEFAULT_LONGITUDE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PERMISSION_DENIED ){
            Toast.makeText(getBaseContext(), "You have to allow the app to acces the location. Please do this and restart the app", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
