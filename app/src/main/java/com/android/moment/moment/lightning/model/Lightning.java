package com.android.moment.moment.lightning.model;

import android.os.Handler;
import android.util.Log;

import com.android.moment.moment.lightning.net.WebSocketClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eluleci on 12/11/14.
 */
public class Lightning implements WebSocketClient.ConnectionStatusListener {

    private static final String TAG = "Lightning";

    private WebSocketClient webSocketClient = new WebSocketClient();

    private Map<String, LightningObject> lightningObjects = new HashMap<String, LightningObject>();

    private WebSocketClient.ConnectionStatusListener connectionStatusListener;

    public Lightning() {
        webSocketClient.setConnectionStatusListener(this);
    }

    public void connect() {
        if (webSocketClient.isConnected()) return;
        webSocketClient.connect();
    }

    public void disconnect() {
        webSocketClient.disconnect();
        reconnectHandler.removeCallbacks(connectTask);  // stop reconnect task
        isConnecting = false;
    }

    public LightningObjectList getList(String className) {
        LightningObjectList lightningObjectList = new LightningObjectList(className);
        lightningObjectList.setLightning(this);
        lightningObjectList.getDataHandler().setWebSocketClient(webSocketClient);
        return lightningObjectList;
    }

    public LightningObject createObject(String className) {
        LightningObject lightningObject = new LightningObject(className);
        lightningObject.setLightning(this);
        lightningObject.getDataHandler().setWebSocketClient(webSocketClient);
        return lightningObject;
    }

    public LightningObject getObject(String res) {

        if (res == null || res.length() == 0) {
            return null;
        }

        if (lightningObjects.containsKey(res)) {
            return lightningObjects.get(res);
        } else {
            LightningObject lightningObject = new LightningObject();
            lightningObject.setLightning(this);
            lightningObject.getDataHandler().setWebSocketClient(webSocketClient);
            lightningObject.setRes(res);
            return lightningObject;
        }
    }

    protected void register(LightningObject lightningObject) {
        if (!lightningObjects.containsKey(lightningObject.getRes())) {
            Log.d(TAG, "Registered " + lightningObject.getRes());
            lightningObjects.put(lightningObject.getRes(), lightningObject);
            webSocketClient.bindSubscription(lightningObject.getRes(), lightningObject.getDataHandler());
        }
    }

    public void registerList(LightningObjectList lightningObjectList) {
        Log.e(TAG, "her " + lightningObjectList.size());
        for (LightningObject lightningObject : lightningObjectList) {
            register(lightningObject);
        }
    }

    /**
     * Connection
     */
    private Handler reconnectHandler = new Handler();
    private Runnable connectTask = new Runnable() {
        public void run() {
            if (webSocketClient.isConnected()) return;

            webSocketClient.connect();
            reconnectHandler.postDelayed(connectTask, 5000);
        }
    };
    private boolean isConnecting;

    public WebSocketClient.ConnectionStatusListener getConnectionStatusListener() {
        return connectionStatusListener;
    }

    public void setConnectionStatusListener(WebSocketClient.ConnectionStatusListener connectionStatusListener) {
        this.connectionStatusListener = connectionStatusListener;
    }

    public boolean isConnected() {
        if (webSocketClient != null) return webSocketClient.isConnected();
        return false;
    }

    @Override
    public boolean onConnectionStatusChange(boolean connected) {

        // don't make any actions if there is no listener
        if (connectionStatusListener == null) return false;

        if (connected) {
            reconnectHandler.removeCallbacks(connectTask);  // stop reconnect task
            isConnecting = false;
            return connectionStatusListener.onConnectionStatusChange(true);

        } else if (!isConnecting) {
            // if not connected and not trying to connect already, ask to the listener for
            // reconnecting or not

            boolean reconnect = connectionStatusListener.onConnectionStatusChange(false);
            if (reconnect) {
                isConnecting = true;
                connectTask.run();
            }
            return reconnect;
        }
        return false;
    }
}
