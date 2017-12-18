package com.sig.galherret.structuresjudiciairessig.model;

import android.content.Context;
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
}
