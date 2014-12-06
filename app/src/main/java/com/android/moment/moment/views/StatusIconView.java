package com.android.moment.moment.views;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;

import com.android.moment.moment.R;
import com.android.moment.moment.lightning.model.Observer;

/**
 * Created by eluleci on 03/12/14.
 */
public class StatusIconView extends View implements Observer<String> {

    public StatusIconView(Context context) {
        super(context);
    }

    public StatusIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void update(String key, String value) {
        ShapeDrawable dot = new ShapeDrawable();
        dot.setShape(new OvalShape());
        if (value.equals("online")) {
            dot.getPaint().setColor(getResources().getColor(R.color.primary_color));
        } else if (value.equals("inactive")) {
            dot.getPaint().setColor(getResources().getColor(R.color.secondary_color));
        } else {
            dot.getPaint().setColor(getResources().getColor(android.R.color.transparent));
        }
        setBackground(dot);
    }
}
