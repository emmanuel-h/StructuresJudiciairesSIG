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
import java.util.logging.Logger;

public class AddLawyerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lawyer);

        // Initializing spinner's datas from an array in strings.xml
        Spinner spinner = findViewById(R.id.profession);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.professions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(l -> sendData());
    }

    /**
     * Function to pass datas about the new lawyer from AddLawyerActivity to MainActivity to be sent
     * afterward to the server
     *
     * Retrieves last known longitude and latitude of the user from intent
     * Retrieves lawyer's informations from the form
     * Deletes all the '&' char since it's used as a separator by the server to handle datas that it receives
     * Sending datas to the main activity through the BroadcastReceiver
     * Ends the activity
     */
    private void sendData(){
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        double latitude = getIntent().getDoubleExtra("latitude", 0);

        EditText nameText = findViewById(R.id.name);
        String name = nameText.getText().toString();
        name = name.replaceAll("&", "");

        EditText forenameText = findViewById(R.id.forename);
        String forename = forenameText.getText().toString();
        forename = forename.replaceAll("&", "");

        EditText addressText = findViewById(R.id.address);
        String address = addressText.getText().toString();
        address = address.replaceAll("&", "");

        EditText phoneText = findViewById(R.id.phoneNumber);
        String phoneNumber = phoneText.getText().toString();
        phoneNumber = phoneNumber.replaceAll("&", "");

        Spinner spinner = findViewById(R.id.profession);
        String profession = spinner.getSelectedItem().toString();

        Intent intent = new Intent("sendData");
        intent.putExtra("name", name);
        intent.putExtra("forename", forename);
        intent.putExtra("address", address);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("profession", profession);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        super.finish();
    }
}
