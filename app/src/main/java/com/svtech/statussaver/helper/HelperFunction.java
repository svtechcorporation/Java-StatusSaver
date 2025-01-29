package com.svtech.statussaver.helper;

import android.content.Context;
import android.widget.Toast;

public class HelperFunction {


    public static void print(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void print(Context ctx, String text, boolean typeLong){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }


}
