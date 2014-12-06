package com.android.moment.moment.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.android.moment.moment.lightning.model.Observer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by eluleci on 10/11/14.
 */
public class UrlImageView extends ImageView implements Observer<String>, Target {

    private String url;

    @Override
    public void update(String key, String value) {
        if (url == null || (value != null && !url.equals(value))) {
            if (url != null) {
                // animate the view only for the first time
                animate().alpha(0.3f).setDuration(300).start();
            }
            url = value;
            Picasso.with(getContext()).load(url).into((Target) this);
        }
    }

    public UrlImageView(Context context) {
        super(context);
    }

    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setImageBitmap(bitmap);
        animate().alpha(1).setDuration(300).start();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }
}
