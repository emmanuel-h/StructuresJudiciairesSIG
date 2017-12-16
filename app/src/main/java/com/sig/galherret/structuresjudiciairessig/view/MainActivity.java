package com.sig.galherret.structuresjudiciairessig.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
    private final String[] files = { "annuaire_ti.json", "annuaire_tgi.json", "annuaire_lieux_justice.json", "liste-des-greffes.json"};

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean status = intent.getBooleanExtra("status",false);
            if(status) {
                for(String file : files) {
                    context.deleteFile(file);
                }
            }
            loadFile();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manageToolbar();
        // We ask the user to access the location
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 42);
        // Launch the localisation service
        launchLocalisationService();
        // Launch the web file
        loadFile();
        // If there is already a SavedInstanceState, we reload only the desired values
        if(null != savedInstanceState){
            latitude = savedInstanceState.getFloat("latitude");
            longitude = savedInstanceState.getFloat("longitude");
        } else {
            // If application just been launched, we check for updates with the server
            updateJson();
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("download"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PERMISSION_DENIED ){
            Toast.makeText(getBaseContext(), "You doesn't allow the app to access the location, some services may not work properly", Toast.LENGTH_LONG).show();
        }
        if (grantResults[0] == PERMISSION_GRANTED){
            launchLocalisationService();
            // We now have the permission, we wait one second and reload the page with the new localisation
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                getPrefs();
                loadFile();
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("latitude",latitude);
        outState.putFloat("longitude",longitude);
    }

    private void launchLocalisationService(){
        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
    }

    private void updateJson(){
        // Instantiate the ProgressBar
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Update informations from the server");
        //mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // Launch the download async task
        final DownloadFile downloadFile = new DownloadFile(MainActivity.this, mProgressDialog);
        try {
            downloadFile.execute("http://"+getServerProperties("IPAddress")+":8080/geojson/"+files[0],
                    "http://"+getServerProperties("IPAddress")+":8080/geojson/"+files[1],
                    "http://"+getServerProperties("IPAddress")+":8080/geojson/"+files[2],
                    "http://"+getServerProperties("IPAddress")+":8080/geojson/"+files[3],
                    files[0],
                    files[1],
                    files[2],
                    files[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mProgressDialog.setOnCancelListener(dialog -> downloadFile.cancel(true));
    }


    private void loadFile(){
        // First we search if there is a known position for the user
        getPrefs();

        // Set up the Webview
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());

        // Replace the properties with the correct values and then load the html file in the browser
        String content;
        try{
            content = IOUtils.toString(getAssets().open("mobileWebPage.html"),Charset.forName("UTF-8"))
                    .replaceAll("%LATITUDE%", String.valueOf(latitude))
                    .replaceAll("%LONGITUDE%", String.valueOf(longitude));
            webView.loadDataWithBaseURL("file:///android_asset/mobileWebPage.html", content, "text/html", "UTF-8", null);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void manageToolbar(){
        Button button = findViewById(R.id.buttonShowMore);
        button.setOnClickListener(l -> loadFile());
    }

    private String getServerProperties(String key) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
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
}
