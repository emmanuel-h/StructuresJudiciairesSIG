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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private float latitude; // 1.9f
    private float longitude; // 47.91f
    private String prenom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageToolbar();

        readPrefs();

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        
        String content;
        try{
            content = IOUtils.toString(getAssets().open("mobileTest.html"));
            String temp = content.replaceAll("%LATITUDE%", String.valueOf(latitude));
            String temp2 = temp.replaceAll("%LONGITUDE%", String.valueOf(longitude));
            webView.loadDataWithBaseURL("file:///android_asset/mobileTest.html", temp2, "text/html", "UTF-8", null);
            //content = IOUtils.toString(getAssets().open("test.html")).replaceAll("%QUI%",getProperty("prenom",getApplicationContext()));
            //webView.loadDataWithBaseURL("file:///android_asset/test.html",content,"text/html","UTF-8",null);
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

    public void readPrefs(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("Data/user.prefs")));
            String line = br.readLine();
            while(null != line){
                if(line.contains("Latitude")){
                    String value = readPrefValue(line);
                    if(value.equals("/")){

                    }else{
                        latitude = Float.parseFloat(value);
                    }
                }
                if(line.contains("Longitude")){
                    String value = readPrefValue(line);
                    if(value.equals("/")){

                    }else{
                        longitude = Float.parseFloat(value);
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : failed to find user.prefs file");
        } catch (IOException e) {
            System.out.println("ERROR : failed to read user.prefs file");
        }
    }

    public String readPrefValue(String line){
        StringTokenizer st = new StringTokenizer(line, "=");
        st.nextToken();
        return st.nextToken();
    }
}
