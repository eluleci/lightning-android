package com.android.moment.moment.lightning;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eluleci on 12/11/14.
 */
public class Lightning {

    private static final String TAG = "Lightning";
    private Map<String, LightningObject> lightningObjects = new HashMap<String, LightningObject>();

    public LightningObjectList getList(String className) {
        LightningObjectList lightningObjectList = new LightningObjectList(className);
        lightningObjectList.setLightning(this);
        return lightningObjectList;
    }

    public LightningObject createObject(String className) {
        LightningObject lightningObject = new LightningObject(className);
        lightningObject.setLightning(this);
        return lightningObject;
    }

    public LightningObject getObject(String res) {

        if (lightningObjects.containsKey(res)) {
            Log.d(TAG, "Returning existing object " + res);
            return lightningObjects.get(res);
        } else {
            LightningObject lightningObject = new LightningObject();
            lightningObject.setLightning(this);
            lightningObject.setRes(res);
            return lightningObject;
        }
    }

    protected void register(LightningObject lightningObject) {
        if (!lightningObjects.containsKey(lightningObject.getRes())) {
            Log.d(TAG, "Registered " + lightningObject.getRes());
            lightningObjects.put(lightningObject.getRes(), lightningObject);
        }
    }

    public void registerList(LightningObjectList lightningObjectList) {
        Log.e(TAG, "her " + lightningObjectList.size());
        for (LightningObject lightningObject : lightningObjectList) {
            register(lightningObject);
        }
    }

}
