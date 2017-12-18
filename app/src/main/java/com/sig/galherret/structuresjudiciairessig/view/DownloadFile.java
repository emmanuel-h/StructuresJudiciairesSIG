package com.sig.galherret.structuresjudiciairessig.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    // Declaration of the progress dialog to be able to communicate with the UI inside the AsyncTask class
    private ProgressDialog mProgressDialog;

    DownloadFile(Context context, ProgressDialog _mProgressDialog) {
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
                // Load the url
                URL url = new URL(urlsAndOutputsfiles[i]);
                // Load the desired output file path
                String outputFile = urlsAndOutputsfiles[i+urlsAndOutputsfiles.length/2];

                // Open the connection to the server
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // Used to displayed the percentage of file downloaded
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
                    // publishing the progress
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (null != output)
                    output.close();
                if (null != input)
                    input.close();
            } catch (IOException ignored) {
            }

            if (null != connection)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Take the CPU lock, to be able to continue the download even if the user ends the application
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
        }
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        // Release the CPU lock
        mWakeLock.release();
        // Ends the progress dialog
        mProgressDialog.dismiss();
        if (result != null) {
            // If there is an error while downloading, alert the main activity
            alertEndDownload(false);
        }
        else {
            // Tell the main activity the download ends
            alertEndDownload(true);
        }
    }

    private void alertEndDownload(boolean value){
        Intent intent = new Intent("download");
        intent.putExtra("status",value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}