package com.android.moment.moment.lightning.model;

/**
 * Created by eluleci on 28/11/14.
 */
public interface Observable {

    public void addObserver(String key, Observer o);

    public void removeObserver(String key, Observer o);

    public void notifyObservers(String key, Object value);
}
