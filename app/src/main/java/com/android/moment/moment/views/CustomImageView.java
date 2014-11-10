package com.android.moment.moment.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.moment.moment.net.model.observer.Field;
import com.android.moment.moment.net.model.observer.FieldObserver;
import com.squareup.picasso.Picasso;

/**
 * Created by eluleci on 10/11/14.
 */
public class CustomImageView extends ImageView implements FieldObserver<String> {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void updateData(Field<String> field, String data) {
        if (data != null) Picasso.with(getContext()).load(data).into(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        setScaleType(ScaleType.CENTER_CROP);
    }
}
