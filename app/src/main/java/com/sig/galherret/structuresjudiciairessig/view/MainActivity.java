package com.sig.galherret.structuresjudiciairessig.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.sig.galherret.structuresjudiciairessig.R;
import com.sig.galherret.structuresjudiciairessig.model.GPSService;
import com.sig.galherret.structuresjudiciairessig.model.JavascriptConnection;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private float longitude;
    private float latitude;
    private final float DEFAULT_LATITUDE = 48.859489f;
    private final float DEFAULT_LONGITUDE = 2.320582f;
    private final String[] files = {"annuaire_ti.json", "annuaire_tgi.json", "annuaire_lieux_justice.json", "liste_des_greffes.json"};
    private String PATH_TO_INTERNAL_STORAGE;
    private boolean addLawyer = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();
            boolean status = intent.getBooleanExtra("status", false);
            switch (action) {
                case "download":
                    if (status) {
                        Toast.makeText(MainActivity.this, "Update complete", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Update not worked", Toast.LENGTH_LONG).show();
                    }
                    loadFile();
                    break;
                case "upload":
                    if(status){
                        Toast.makeText(MainActivity.this, "Upload complete", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    }
                    break;
                case "loadWebsite":
                    String url = intent.getStringExtra("url");
                    if (null == url) {
                        Toast.makeText(MainActivity.this, "Could not load this website", Toast.LENGTH_LONG).show();
                    } else {
                        Intent mIntent = new Intent(MainActivity.this, WebViewActivity.class);
                        mIntent.putExtra("url", url);
                        startActivity(mIntent);
                    }
                    break;
                case "makeCall":
                    String phoneNumber = intent.getStringExtra("phoneNumber");
                    if (null == phoneNumber) {
                        Toast.makeText(MainActivity.this, "Could not call this phone number", Toast.LENGTH_LONG).show();
                    } else {
                        Intent mIntent = new Intent(Intent.ACTION_DIAL);
                        mIntent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(mIntent);
                    }
                    break;
                case "updateLocation":
                    double longitude = intent.getDoubleExtra("longitude", 0);
                    double latitude = intent.getDoubleExtra("latitude", 0);
                    webView.loadUrl("javascript:updateLocation(" + longitude + ", " + latitude + ")");
                    break;
                case "addLawyer":
                    double longitudeLawyer = intent.getDoubleExtra("longitude", 0);
                    double latitudeLawyer = intent.getDoubleExtra("latitude", 0);
                    Intent mIntent = new Intent(MainActivity.this, AddLawyerActivity.class);
                    startActivity(mIntent);
                    break;
                case "sendFile":
                    String server = intent.getStringExtra("server");
                    String fileName = intent.getStringExtra("fileName");
                    ProgressDialog mProgressDialog;
                    mProgressDialog = new ProgressDialog(MainActivity.this);
                    mProgressDialog.setMessage("Update informations from the server");
                    //mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    UploadFile upload = new UploadFile(MainActivity.this, mProgressDialog);
                    upload.execute(server, fileName);
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Nothing received", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PATH_TO_INTERNAL_STORAGE = getFilesDir().getAbsolutePath();
        manageToolbar();
        // We ask the user to access the location
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 42);
        // Launch the localisation service
        launchLocalisationService();
        webView = findViewById(R.id.webView);
        // If there is already a SavedInstanceState, we reload only the desired values
        if (null != savedInstanceState) {
            latitude = savedInstanceState.getFloat("latitude");
            longitude = savedInstanceState.getFloat("longitude");
        } else {
            // If application just been launched, we check for updates with the server
            updateJson();
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("download"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("upload"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("loadWebsite"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("makeCall"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("updateLocation"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("addLawyer"));
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("sendFile"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PERMISSION_DENIED) {
            Toast.makeText(getBaseContext(), "You doesn't allow the app to access the location, some services may not work properly", Toast.LENGTH_LONG).show();
        }
        if (grantResults[0] == PERMISSION_GRANTED) {
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
        outState.putFloat("latitude", latitude);
        outState.putFloat("longitude", longitude);
    }

    private void launchLocalisationService() {
        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
    }

    private void updateJson() {
        // Instantiate the ProgressBar
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Update informations from the server");
        //mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        // Launch the download async task
        final DownloadFile downloadFile = new DownloadFile(MainActivity.this, mProgressDialog);
        try {
            downloadFile.execute("http://" + getServerProperties("IPAddress") + ":8080/geojson/" + files[0],
                    "http://" + getServerProperties("IPAddress") + ":8080/geojson/" + files[1],
                    "http://" + getServerProperties("IPAddress") + ":8080/geojson/" + files[2],
                    "http://" + getServerProperties("IPAddress") + ":8080/geojson/" + files[3],
                    files[0],
                    files[1],
                    files[2],
                    files[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mProgressDialog.setOnCancelListener(dialog -> downloadFile.cancel(true));
    }


    private void loadFile() {
        // First we search if there is a known position for the user
        getPrefs();

        // Set up the Webview
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavascriptConnection(MainActivity.this), "JSInterface");

        // Replace the properties with the correct values and then load the html file in the browser
        String content;
        try {
            content = IOUtils.toString(getAssets().open("mobileWebPage/mobileWebPage.html"), Charset.forName("UTF-8"))
                    .replaceAll("%LATITUDE%", String.valueOf(latitude))
                    .replaceAll("%LONGITUDE%", String.valueOf(longitude))
                    .replaceAll("%PATH_TO_INTERNAL_STORAGE%", String.valueOf(PATH_TO_INTERNAL_STORAGE));
            webView.loadDataWithBaseURL("file:///android_asset/mobileWebPage/mobileWebPage.html", content, "text/html", "UTF-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.greffe:
                dispLayer(item, "vectorLayerListeGreffes");
                return true;
            case R.id.tgi:
                dispLayer(item, "vectorLayerTgi");
                return true;
            case R.id.ti:
                dispLayer(item, "vectorLayerTi");
                return true;
            case R.id.lieuxJustice:
                dispLayer(item, "vectorLayerLieuxJustice");
                return true;
            case R.id.personnes:
                return true;
            case R.id.clearItinerary:
                clearItinerary();
                return true;
            default:
                return false;
        }
    }

    private void dispLayer(MenuItem item, String layerName) {
        if (item.isChecked()) {
            Logger.getAnonymousLogger().severe("layer name : " + layerName);
            webView.loadUrl("javascript:dispLayer('" + layerName + "', " + false + ")");
            item.setChecked(false);
        } else {
            webView.loadUrl("javascript:dispLayer('" + layerName + "', " + true + ")");
            item.setChecked(true);
        }
    }

    private void manageToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button buttonShowMore = findViewById(R.id.buttonRefresh);
        buttonShowMore.setOnClickListener(l -> loadFile());
        Button buttonAdd = findViewById(R.id.buttonAddLawyer);
        buttonAdd.setOnClickListener(l -> addLawyer());
        Button buttonCenter = findViewById(R.id.buttonCenter);
        buttonCenter.setOnClickListener(l -> centerMap());
    }

    private void centerMap() {
        webView.loadUrl("javascript:centerMap()");
    }

    private String getServerProperties(String key) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("server.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    private void getPrefs() {
        SharedPreferences userPrefs = getSharedPreferences("coordinates", MODE_PRIVATE);
        // If there is no known position, we center the map on Paris
        latitude = userPrefs.getFloat("lastKnownLatitude", DEFAULT_LATITUDE);
        longitude = userPrefs.getFloat("lastKnownLongitude", DEFAULT_LONGITUDE);
    }

    private void clearItinerary() {
        webView.loadUrl("javascript:clearItinerary()");
    }

    private void addLawyer() {
        Button button = findViewById(R.id.buttonAddLawyer);
        if (!addLawyer) {
            button.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            addLawyer = true;
            webView.loadUrl("javascript:setAddLawyer(" + true + ")");
        } else {
            button.setBackground(getResources().getDrawable(android.R.drawable.ic_input_add));
            addLawyer = false;
            webView.loadUrl("javascript:setAddLawyer(" + false + ")");
        }
    }
}
