package com.android.moment.moment.net.core.handler;

import com.android.moment.moment.net.model.component.ResourcePath;
import com.android.moment.moment.net.core.message.Message;
import com.android.moment.moment.net.core.message.PushMessage;

import org.json.JSONException;


public abstract class DeleteHandler extends MessageHandler<ResourcePath> {

    private String id;

    public DeleteHandler(String id){
        this.id = id;
    }

    @Override
    public Message prepareMessage() {
        return new Message.Builder().cmd(Message.Command.DELETE).id(id).build();
    }

    @Override
    public ResourcePath onReceivePushMessage(PushMessage pushMessage) throws JSONException {
        return null;
    }
}
