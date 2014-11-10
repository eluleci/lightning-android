package com.android.moment.moment.net.core.message;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class stores the options of both request and response messages.
 *
 */
public class MessageOptions {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public JSONObject js() {
        try {
            return new JSONObject(new Gson().toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
