package com.objectivetruth.uoitlibrarybooking.userinterface.common;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceSingleton {
    private static TypefaceSingleton instance = new TypefaceSingleton();
    private TypefaceSingleton() {}
    private Typeface mTypeFace = null;

    public static TypefaceSingleton getInstance() {
        return instance;
    }
    public Typeface getTypeface(Context context) {
        if(mTypeFace == null){
            mTypeFace = Typeface.createFromAsset(context.getResources().getAssets(), "roboto_light.ttf");
        }
        return mTypeFace;
    }
}