package com.sig.galherret.structuresjudiciairessig.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by benoit on 20/12/17.
 */

public class UploadFile extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressDialog mProgressDialog;

    public UploadFile(Context context, ProgressDialog progressDialog){
        this.context = context;
        this.mProgressDialog = progressDialog;
    }

    @Override
    protected String doInBackground(String... strings) {
        String attachmentName = "newLawyer";
        String attachmentFileName = "newLawyer.txt";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0] + strings[1] + ".txt");
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Controle", "no-cache");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.connect();

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            FileInputStream input = context.openFileInput(strings[1]);
            /*
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while((count = input.read(data)) != -1){
                String s = new String(data);
                Logger.getAnonymousLogger().severe("data : " + s);
                total += count;
                request.write(data, 0, count);
            }
            */

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            //int bufferSize = input.available();
            //byte[] buffer = new byte[bufferSize];
            //input.read(buffer, 0, bufferSize);
            //request.write(buffer, 0, bufferSize);
            request.writeBytes("lul mdr");

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();
            input.close();

            Logger.getAnonymousLogger().severe("server connection : " + connection.getResponseMessage() + " (" + connection.getResponseCode() + ")");

            InputStream response = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(response));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            br.close();
            response.close();
            connection.disconnect();
            Logger.getAnonymousLogger().severe("reponse : " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

        /*
        OutputStream output = null;
        FileInputStream input = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            String inputFile = strings[1];
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getAnonymousLogger().severe("response : " + connection.getResponseMessage() + " (" + connection.getResponseCode() + ")");
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();

            output = connection.getOutputStream();
            input = context.openFileInput(inputFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while((count = input.read(data)) != -1){
                if(isCancelled()){
                    input.close();
                    return null;
                }
                total += count;
                if(fileLength > 0){
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(null != output) {
                        output.close();
                    }
                    if(null != input){
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(null != connection){
                    connection.disconnect();
            }
        }
        return null;
        */
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if(null != result){
            alertEndUpload(false);
        }else{
            alertEndUpload(true);
        }
    }

    private void alertEndUpload(boolean value){
        Intent intent = new Intent("upload");
        intent.putExtra("status",value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
