package com.android.moment.moment.lightning.model;

/**
 * Created by eluleci on 11/11/14.
 */
public interface Observer<T> {
    public void update(String key, T value);
}
