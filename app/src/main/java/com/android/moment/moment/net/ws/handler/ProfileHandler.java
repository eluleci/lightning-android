package com.android.moment.moment.net.ws.handler;

import android.util.Log;

import com.android.moment.moment.net.core.handler.MessageHandler;
import com.android.moment.moment.net.core.handler.ModelMessageHandler;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.MessageError;
import com.android.moment.moment.net.core.message.MessageOptions;
import com.android.moment.moment.net.core.message.PushMessage;
import com.android.moment.moment.net.model.Profile;
import com.android.moment.moment.net.ws.message.GetOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileHandler extends ModelMessageHandler<Profile> {

    private static final String TAG = "ProfileHandler";

    public ProfileHandler(String id) {
        model = new Profile();
        model.setId(id);
    }

    @Override
    public Message prepareMessage() {

        MessageOptions opts = new GetOptions(subscribeOnExecute, fields);
        return new Message.Builder().cmd(Message.Command.GET).id(model.getId()).opts(opts).build();
    }

    @Override
    public Profile onReceiveResponse(Message responseMessage) throws JSONException {
        return model;
    }

    @Override
    public void onError(MessageHandler messageHandler, MessageError error) {
        super.onError(messageHandler, error);
    }

    @Override
    public Profile onReceivePushMessage(PushMessage pushMessage) throws JSONException {

        Log.d(TAG, "Push message received to handler");

        JSONObject body = pushMessage.getBody();

        if (body.has("name")) model.setName(body.getString("name"));
        if (body.has("avatar")) model.setAvatar(body.getString("avatar"));
        return model;
    }

}
