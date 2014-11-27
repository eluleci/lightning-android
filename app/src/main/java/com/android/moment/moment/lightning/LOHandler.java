package com.android.moment.moment.lightning;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by eluleci on 11/11/14.
 */
public class LOHandler extends MessageHandler<LightningObject> {

    private LightningObject model;

    private Message.Command command;

    private Message message;

    public LOHandler(LightningObject model) {
        this.model = model;
    }

    public void sendSaveMessage(JSONObject body) {
        command = Message.Command.POST;
        message = new Message.Builder().cmd(command).res(model.getRes()).body(body).build();
        execute();
    }

    @Override
    public Message prepareMessage() {
        return message;
    }

    @Override
    public LightningObject onReceiveResponse(Message responseMessage) throws JSONException {
        if (responseMessage.getBody() == null) {
            // TODO handle update response
            // TODO detect the response type and clear the unsaved changes if it is save result
            model.cleanChanges();
        } else {
            model.setRes(responseMessage.getRes());
            updateBody(responseMessage.getBody());
        }
        return model;
    }

    @Override
    public LightningObject onReceivePushMessage(PushMessage pushMessage) throws JSONException {
        if (pushMessage.getBody() != null)
            updateBody(pushMessage.getBody());
        return model;
    }

    public void setCommand(Message.Command command) {
        this.command = command;
    }

    private void updateBody(JSONObject body) {
        Iterator<String> iterator = body.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object value = body.get(key);
                model.setClean(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
