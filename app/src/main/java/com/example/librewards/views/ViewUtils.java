package com.example.librewards.views;

import android.content.Context;
import android.widget.Toast;

public class ViewUtils {
    private ViewUtils(){
    }

    public static void toastMessage(String message, Context c){
        Toast.makeText(c.getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
