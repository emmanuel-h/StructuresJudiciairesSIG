package com.sig.galherret.structuresjudiciairessig.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by emmanuelh on 15/12/17.
 */

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadFile extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog mProgressDialog;

    public DownloadFile(Context context, ProgressDialog _mProgressDialog) {
        this.context = context;
        this.mProgressDialog = _mProgressDialog;
    }

    @Override
    protected String doInBackground(String... urlsAndOutputsfiles) {
        InputStream input = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;
        try {
            for(int i = 0 ; i < urlsAndOutputsfiles.length/2 ; i++){
                URL url = new URL(urlsAndOutputsfiles[i]);
                String outputFile = urlsAndOutputsfiles[i+urlsAndOutputsfiles.length/2];
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = context.openFileOutput(outputFile, Context.MODE_PRIVATE);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null) {
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            Intent intent = new Intent("download");
            intent.putExtra("status",false);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        else {
            Intent intent = new Intent("download");
            intent.putExtra("status",true);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }


    private void compareFiles(){
        String[] files = context.fileList();
        InputStream json_old;
        InputStream json_new;
        String file = files[1];
        try {
            json_new = context.openFileInput(file);
            int size = json_new.available();
            byte[] buffer = new byte[size];
            json_new.read(buffer);
            json_new.close();
            String myJson = new String(buffer, "UTF-8");
            JSONObject objJsonNew = new JSONObject(myJson);

            json_old = context.getAssets().open("annuaire_ti.json");
            int sizeOld = json_old.available();
            byte[] bufferOld = new byte[sizeOld];
            json_old.read(bufferOld);
            json_old.close();
            String myJsonOld = new String(bufferOld, "UTF-8");
            JSONObject objJsonOld = new JSONObject(myJsonOld);

            JSONArray jsonArrayNew = objJsonNew.getJSONArray("features");
            JSONArray jsonArrayOld = objJsonOld.getJSONArray("features");

            if(jsonArrayNew.length()!=jsonArrayOld.length()){
                // An object was added
                System.out.println("PROBLEME TAILLE");
            }

            // Test if all objects are the same
            for(int i = 0; i < jsonArrayNew.length() ; i++){
                JSONObject objJsonOldInside = jsonArrayOld.getJSONObject(i);
                JSONObject objJsonNewInside = jsonArrayNew.getJSONObject(i);
//                JSONArray test= objJsonNewInside.getJSONArray("properties");
               // System.out.println(((JSONObject)objJsonNewInside.get("properties")).keys());
                for(Iterator<String> iterator = ((JSONObject)objJsonNewInside.get("properties")).keys() ; iterator.hasNext();){
                    String key = iterator.next();
                    if(!(((JSONObject)objJsonNewInside.get("properties")).get(key).equals(((JSONObject)objJsonOldInside.get("properties")).get(key)))){
                        System.out.println("update json : "+((JSONObject)objJsonNewInside.get("properties")).get(key));
                        ((JSONObject)objJsonOldInside.get("properties")).remove(key);
                        ((JSONObject)objJsonOldInside.get("properties")).put(key,((JSONObject)objJsonNewInside.get("properties")).get(key));
                    }
                }
                /*
                if(!objJsonOldInside.get("properties").equals(objJsonNewInside.get("properties"))){
                    // An object is not the same in both file, we modify the old one
                    System.out.println("PROBLEME COMPARAISON");
                    System.out.println(objJsonOldInside.get("properties"));
                    System.out.println(objJsonNewInside.get("properties"));
                    objJsonOldInside.remove("properties");
                    objJsonOldInside.put("properties",objJsonNewInside.get("properties"));
                } else {
                    System.out.println("OK COMPARAISON");
                }*/
            }
/*
            for(Iterator<String> iterator = objJsonNew.getJSONArray("features").keys(); iterator.hasNext();) {
                String key = iterator.next();
                Object valueNew = objJsonNew.get(key);
                Object valueOld = objJsonOriginal.get(key);
                if(valueNew != valueOld){
                    System.out.println("ERROR "+valueNew.toString()+" - "+valueOld.toString());
                }
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}