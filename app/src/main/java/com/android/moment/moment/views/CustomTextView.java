package com.android.moment.moment.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;

/**
 * Created by eluleci on 10/11/14.
 */
public class CustomTextView extends TextView implements FieldObserver<String> {

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
    public void updateData(Field<String> field, String data) {
        if (data != null) setText(data);
    }
}
