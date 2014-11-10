package com.android.moment.moment.net.model;

import com.android.moment.moment.net.model.observer.Field;

import org.json.JSONObject;

import java.util.List;

public interface ObservableList<E> extends Model, List<E> {

    public static final Field<ObservableList<?>> LIST = new Field<ObservableList<?>>("list");
    public static final Field<JSONObject> METADATA = new Field<JSONObject>("metadata");
    public static final Field<String> ENTRIES_OBJECT = new Field<String>("object");
    public static final Field<String> ENTRIES_STATUS = new Field<String>("status");
    public static final Field<String> ENTRIES_WHEN = new Field<String>("when");

    /**
     *
     * @return the metadata object of the list
     */
    public JSONObject getMetadata();

    /**
     * sets the metadata object of the list
     * @param metadata the new metadata object to be set
     */
    public void setMetadata(JSONObject metadata);

    /**
     * clears the list without notifying its observers
     */
    public void clearSilent();

    /**
     * isReceived is true if data has been received, list can still be empty if data was empty too
     * @return true if list-data has been received
     */
    public boolean isReceived();

    /**
     * sets received-state if data has been received
     * @param isReceived the state to be set, if data is received
     */
    public void setReceived(boolean isReceived);


    /**
     *
     * @return true if list is ordered ascending
     */
    public boolean isAscending();

    /**
     * sets the ordering of the list
     * @param ascending true for ascending order of the list
     */
    public void setAscending(boolean ascending);
}