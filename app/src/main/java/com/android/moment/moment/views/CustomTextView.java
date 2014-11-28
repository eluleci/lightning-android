package com.android.moment.moment.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.moment.moment.lightning.model.Observer;

/**
 * Created by eluleci on 10/11/14.
 */
public class CustomTextView extends TextView implements Observer {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void update(String key, Object value) {
        if (value != null && value instanceof String) setText(value.toString());
        if (value != null && value instanceof Integer) setText(Integer.toString((Integer) value));
    }
}
