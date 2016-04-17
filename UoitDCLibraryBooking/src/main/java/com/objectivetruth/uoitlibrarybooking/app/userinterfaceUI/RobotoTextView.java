package com.objectivetruth.uoitlibrarybooking.app.userinterfaceUI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.TypefaceSingleton;

public class RobotoTextView extends TextView {

    public RobotoTextView(Context context) {
        super(context);
        style(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            style(context);
        }
    }

    private void style(Context context) {
        if (!isInEditMode()) {
            Typeface tf = TypefaceSingleton.getInstance().getTypeface(context);
            setTypeface(tf);
        }

    }

}