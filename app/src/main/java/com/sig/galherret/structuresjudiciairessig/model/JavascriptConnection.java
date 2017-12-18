package com.sig.galherret.structuresjudiciairessig.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavascriptConnection {

    private Context context;

    public JavascriptConnection(Context _context){
        this.context = _context;
    }

    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(context,toast,Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void loadWebsite(String url){
        Intent intent = new Intent("loadWebsite");
        intent.putExtra("url", url);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @JavascriptInterface
    public void makeCall(String phoneNumber){
        Intent intent = new Intent("makeCall");
        intent.putExtra("phoneNumber", phoneNumber);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @JavascriptInterface
    public void calcDistance(float longitude, float latitude){
        Intent intent = new Intent("calcDistance");
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
