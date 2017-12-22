package com.sig.galherret.structuresjudiciairessig.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sig.galherret.structuresjudiciairessig.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class AddLawyerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lawyer);

        Spinner spinner = findViewById(R.id.profession);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.professions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(l -> createFile());
    }

    private void createFile(){
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        EditText nameText = findViewById(R.id.name);
        String name = nameText.getText().toString();
        EditText forenameText = findViewById(R.id.forename);
        String forename = forenameText.getText().toString();
        EditText addressText = findViewById(R.id.address);
        String address = addressText.getText().toString();
        EditText phoneText = findViewById(R.id.phoneNumber);
        String phoneNumber = phoneText.getText().toString();
        Spinner spinner = findViewById(R.id.profession);
        String profession = spinner.getSelectedItem().toString();
        try {
            FileOutputStream fos = openFileOutput("newLawyer", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write("name:" + name + ","
                    + "forename:" + forename + ","
                    + "address:" + address + ","
                    + "phone:" + phoneNumber + ","
                    + "profession:" + profession);
            osw.close();
            fos.close();
            sendFile("newLawyer");
        } catch (IOException e) {
            Toast.makeText(this, "Cannot add a lawyer", Toast.LENGTH_LONG).show();
        }
    }

    private void sendFile(String file) throws IOException {
        String serverAddress = "http://" + getServerProperties("IPAddress") + ":8080/geojson/";
        Intent intent = new Intent("sendFile");
        intent.putExtra("server", serverAddress);
        intent.putExtra("fileName", file);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String getServerProperties(String key) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("server.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
