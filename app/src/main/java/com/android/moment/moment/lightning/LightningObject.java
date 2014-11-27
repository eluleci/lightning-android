package com.android.moment.moment.lightning;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by eluleci on 11/11/14.
 */
public class LightningObject {

    private final String ALL = "all";

    private final String TAG = "LightningObject:";
    private boolean syncing = false;

    protected String res;
    protected String id;
    protected JSONObject body = new JSONObject();
    protected JSONObject unsavedChanges;
    private Map<String, List<Observer>> observers = new HashMap<String, List<Observer>>();

    private LOHandler dataHandler = new LOHandler(this);

    public LightningObject(String className) {
        this.body = new JSONObject();
        try {
            this.body.put("className", className);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LightningObject(JSONObject body) {
        setBody(body);
    }

    public LightningObject(String name, String id) {
        this(name);
        setId(id);
    }

    public void addObserver(Observer o) {
        List<Observer> obs = observers.get(ALL);

        if (obs == null) {
            obs = new ArrayList<Observer>();
            observers.put(ALL, obs);
        }
        obs.add(o);
    }

    public void addObserver(String key, Observer o) {
        List<Observer> obs = observers.get(key);

        if (obs == null) {
            obs = new ArrayList<Observer>();
            observers.put(key, obs);
        }
        obs.add(o);

        // notifying new observers with the current data
        if (get(key) != null) notifyObservers(key, get(key));
    }

    public void notifyObservers(String key, Object data) {
        // notifying the observers of the specified key
        List<Observer> obs = observers.get(key);

        if (obs == null) return;
        for (Observer o : obs) {
            o.update(key, data);
        }

        // notifying all observers
        obs = observers.get(ALL);
        if (obs == null) return;
        for (Observer o : obs) {
            o.update(key, data);
        }
    }

    public void removeObserver(String key, Observer observer) {

    }

    public String getClassName() {
        try {
            return body.getString("className");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
        WebSocketClient.getInstance().bindSubscription(res, dataHandler);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void set(String key, Object value) {
        if (key.equals("id")) setId(value.toString());

        try {
            body.put(key, value);
            notifyObservers(key, value);

            // if id is not null it means that the object is synced with server
            // so save the changes to sync the data later
            if (!key.equals("id") && id != null) {
                if (unsavedChanges == null) {
                    unsavedChanges = new JSONObject();
                }
                unsavedChanges.put(key, value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This methods sets a value in a key but doesn't make the object dirty. It means there won't be
     * any change to save.
     *
     * @param key
     * @param value
     */
    public void setClean(String key, Object value) {
        if (key.equals("id")) setId(value.toString());

        try {
            body.put(key, value);
            notifyObservers(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        if (!body.has("className")) Log.d(TAG, "There must be className inside the body");

        // calling setter for each field in body
        Iterator<String> iterator = body.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object value = body.get(key);
                setClean(key, value);
                if (key.equals("id")) setId(value.toString());
                if (key.equals("res")) setRes(value.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanChanges() {
        unsavedChanges = null;
    }

    public Object get(String key) {
        if (!body.has(key)) return null;

        try {
            return body.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String key) {
        if (!body.has(key)) return "";

        try {
            if (body.get(key).getClass().equals(String.class))
                return (String) body.get(key);
            else if (body.get(key) instanceof Number)
                return body.get(key) + "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Integer getInt(String key) {
        if (!body.has(key)) return 0;

        try {
            return (Integer) body.get(key);
        } catch (ClassCastException cce) {
            printError("The field '" + key + "' cannot be cast to Integer.");
            cce.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Float getFloat(String key) {
        if (!body.has(key)) return 0f;

        try {
            return (Float) body.get(key);
        } catch (ClassCastException cce) {
            printError("The field '" + key + "' cannot be cast to Float.");
            cce.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public Double getDouble(String key) {
        if (!body.has(key)) return 0d;

        try {
            return (Double) body.get(key);
        } catch (ClassCastException cce) {
            printError("The field '" + key + "' cannot be cast to Double.");
            cce.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    private void printError(String error) {
        Log.e(TAG + getClassName(), error);
    }

    public void setSyncing(boolean syncing) {
        this.syncing = syncing;
    }

    public void save() {

        // if object has no res path, set class name as res
        if (getRes() == null) setRes(getClassName());

        if (id == null) {
            // if res is null it means that the object is created locally, so save the whole body
            dataHandler.sendSaveMessage(body);

        } else if (unsavedChanges != null) {
            // if res is not null and there are unsaved changes it means we need to sync the changes
            dataHandler.sendSaveMessage(unsavedChanges);

        }
    }
}
