package com.android.moment.moment.lightning.net;

import android.util.Log;

import com.android.moment.moment.lightning.message.Message;
import com.android.moment.moment.lightning.message.MessageError;
import com.android.moment.moment.lightning.message.PushMessage;
import com.android.moment.moment.lightning.model.LightningObject;
import com.android.moment.moment.lightning.model.LightningObjectList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eluleci on 11/11/14.
 */
public class LOListHandler extends MessageHandler<List<LightningObject>> {

    private static final String TAG = "LOListHandler";
    private LightningObjectList model;

    private Message.Command command;
    private Message message;

    public LOListHandler(LightningObjectList model) {
        this.model = model;
    }

    public void getListData() {
        command = Message.Command.GET;
        message = new Message.Builder().cmd(command).res(model.getRes()).build();
        execute();
    }

    public void saveNewItem(JSONObject body) {
        command = Message.Command.POST;
        message = new Message.Builder().cmd(command).res(model.getRes()).body(body).build();
//        System.out.println(message);
        execute();
    }

    @Override
    public Message prepareMessage() {
        System.out.println(model.getLightning().toString());
        return message;
    }

    @Override
    public List<LightningObject> onReceiveResponse(Message responseMessage) throws JSONException {

        if (responseMessage.getStatus() == 200) {

            if (responseMessage.getBody().has("list")) {
                // this is response for getting the list
                updateBody(responseMessage.getBody().getJSONArray("list"));
            } else if (responseMessage.getBody().has("res")) {
                // this is response for adding new item
            }
        } else {
            if (messageActionListener != null) {
                messageActionListener.onError(this, new MessageError(404, "Not found"));
            }
            Log.e(TAG, "Error happened.");
        }
        return model;
    }

    @Override
    public List<LightningObject> onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        Log.d(TAG, "Received push message " + model.size());

        if (pushMessage.getMasterCmd().equals(Message.Command.POST)) {
            // new item added to the list
            Log.d(TAG, "Push message is an object creation message. s: " + model.size());

            // this is a new object that is created by some other person
            LightningObject lightningObject = model.getLightning().getObject(pushMessage.getBody().getString("res"));
            lightningObject.setBody(pushMessage.getBody());
            model.add(lightningObject);
        }

        return model;
    }

    private void updateBody(JSONArray array) throws JSONException {

        model.clear();
        List<LightningObject> list = new ArrayList<LightningObject>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject body = array.getJSONObject(i);
            LightningObject lightningObject = model.getLightning().getObject(body.getString("res"));
            lightningObject.setBody(body);
            list.add(lightningObject);
        }
        model.addAll(list);
    }

}
